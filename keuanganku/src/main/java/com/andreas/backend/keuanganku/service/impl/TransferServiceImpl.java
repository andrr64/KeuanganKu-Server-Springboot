// service/impl/TransferServiceImpl.java
package com.andreas.backend.keuanganku.service.impl;

import com.andreas.backend.keuanganku.dto.AkunTransfer;
import com.andreas.backend.keuanganku.dto.request.TransferRequest;
import com.andreas.backend.keuanganku.dto.request.UpdateTransferRequest;
import com.andreas.backend.keuanganku.dto.response.DetailTransferResponse;
import com.andreas.backend.keuanganku.dto.response.TransferResponse;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.model.Transfer;
import com.andreas.backend.keuanganku.repository.AkunRepository;
import com.andreas.backend.keuanganku.repository.PenggunaRepository;
import com.andreas.backend.keuanganku.repository.TransferRepository;
import com.andreas.backend.keuanganku.service.TransferService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepo;
    private final AkunRepository akunRepo;
    private final PenggunaRepository penggunaRepo;


    private TransferResponse toResponse(Transfer t) {
        return new TransferResponse(
                t.getId(),
                new AkunTransfer(t.getDariAkun().getId(), t.getDariAkun().getNama()),
                new AkunTransfer(t.getKeAkun().getId(), t.getKeAkun().getNama()),
                t.getJumlah(),
                t.getTanggal(),
                t.getCatatan()
        );
    }

    @Override
    public TransferResponse lakukanTransfer(UUID idPengguna, TransferRequest req) {
        if (req.getIdDariAkun().equals(req.getIdKeAkun())) {
            throw new IllegalArgumentException("Akun pengirim dan penerima tidak boleh sama");
        }

        Akun dari = akunRepo.findByIdAndPengguna_Id(req.getIdDariAkun(), idPengguna)
                .orElseThrow(() -> new EntityNotFoundException("Akun pengirim tidak ditemukan"));
        Akun ke = akunRepo.findByIdAndPengguna_Id(req.getIdKeAkun(), idPengguna)
                .orElseThrow(() -> new EntityNotFoundException("Akun penerima tidak ditemukan"));

        if (req.getJumlah().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0");
        }

        if (dari.getSaldo().compareTo(req.getJumlah()) < 0) {
            throw new IllegalArgumentException("Saldo akun pengirim tidak cukup");
        }

        // Parse ISO 8601 string ke OffsetDateTime
        OffsetDateTime tanggal = parseIsoDateTime(req.getTanggal());

        // Update saldo
        dari.setSaldo(dari.getSaldo().subtract(req.getJumlah()));
        ke.setSaldo(ke.getSaldo().add(req.getJumlah()));
        akunRepo.saveAll(List.of(dari, ke));

        Transfer transfer = new Transfer();
        transfer.setPengguna(penggunaRepo.findById(idPengguna)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan")));
        transfer.setDariAkun(dari);
        transfer.setKeAkun(ke);
        transfer.setJumlah(req.getJumlah());
        transfer.setTanggal(tanggal);
        transfer.setCatatan(req.getCatatan());

        return toResponse(transferRepo.save(transfer));
    }

    @Override
    public List<TransferResponse> getAllTransfer(UUID idPengguna, UUID idAkun, String startDate, String endDate, String sort) {
        OffsetDateTime start = parseIsoDateOrNull(startDate);
        OffsetDateTime end = parseIsoDateOrNull(endDate);

        List<Transfer> all = transferRepo.findAllByPengguna_Id(idPengguna);
        List<Transfer> hasil = new ArrayList<>();

        for (Transfer t : all) {
            if (idAkun != null
                    && !t.getDariAkun().getId().equals(idAkun)
                    && !t.getKeAkun().getId().equals(idAkun)) {
                continue;
            }

            if (start != null && t.getTanggal().isBefore(start)) {
                continue;
            }
            if (end != null && t.getTanggal().isAfter(end)) {
                continue;
            }

            hasil.add(t);
        }

        hasil.sort((a, b) -> "asc".equalsIgnoreCase(sort)
                ? a.getTanggal().compareTo(b.getTanggal())
                : b.getTanggal().compareTo(a.getTanggal()));

        return hasil.stream()
                .map(this::toResponse)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public DetailTransferResponse getById(UUID idPengguna, UUID idTransfer) {
        Transfer t = transferRepo.findById(idTransfer)
                .orElseThrow(() -> new EntityNotFoundException("Transfer tidak ditemukan"));

        if (!t.getPengguna().getId().equals(idPengguna)) {
            throw new IllegalArgumentException("Anda tidak memiliki akses ke transfer ini");
        }

        return new DetailTransferResponse(
                t.getId(),
                t.getDariAkun().getId(),
                t.getDariAkun().getNama(),
                t.getKeAkun().getId(),
                t.getKeAkun().getNama(),
                t.getJumlah(),
                t.getTanggal(),
                t.getCatatan()
        );
    }

    @Override
    public void deleteTransfer(UUID idPengguna, UUID idTransfer) {
        Transfer t = transferRepo.findById(idTransfer)
                .orElseThrow(() -> new EntityNotFoundException("Transfer tidak ditemukan"));

        if (!t.getPengguna().getId().equals(idPengguna)) {
            throw new IllegalArgumentException("Anda tidak memiliki akses ke transfer ini");
        }

        Akun akunDari = akunRepo.findById(t.getDariAkun().getId())
                .orElseThrow(() -> new EntityNotFoundException("Akun pengirim tidak ditemukan"));
        Akun akunKe = akunRepo.findById(t.getKeAkun().getId())
                .orElseThrow(() -> new EntityNotFoundException("Akun penerima tidak ditemukan"));

        BigDecimal jumlah = t.getJumlah();

        if (akunKe.getSaldo().compareTo(jumlah) < 0) {
            throw new IllegalArgumentException("Saldo akun penerima tidak mencukupi untuk rollback transfer");
        }

        akunDari.setSaldo(akunDari.getSaldo().add(jumlah));
        akunKe.setSaldo(akunKe.getSaldo().subtract(jumlah));

        akunRepo.save(akunDari);
        akunRepo.save(akunKe);

        transferRepo.delete(t);
    }

    @Override
    public TransferResponse updateTransfer(UUID idPengguna, UUID idTransfer, UpdateTransferRequest request) {
        Transfer transfer = transferRepo.findById(idTransfer)
                .orElseThrow(() -> new EntityNotFoundException("Transfer tidak ditemukan"));

        if (!transfer.getPengguna().getId().equals(idPengguna)) {
            throw new IllegalArgumentException("Anda tidak memiliki akses ke transfer ini");
        }

        boolean isUpdated = false;

        if (request.getTanggal() != null && !request.getTanggal().isBlank()) {
            OffsetDateTime tanggalBaru = parseIsoDateTime(request.getTanggal());
            transfer.setTanggal(tanggalBaru);
            isUpdated = true;
        }

        if (request.getCatatan() != null) {
            transfer.setCatatan(request.getCatatan());
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new IllegalArgumentException("Minimal satu field (tanggal atau catatan) harus diisi");
        }

        transferRepo.save(transfer);
        return toResponse(transfer);
    }

    // Helper: Parse ISO 8601 string ke OffsetDateTime
    private OffsetDateTime parseIsoDateTime(String input) {
        try {
            return OffsetDateTime.parse(input);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal harus ISO 8601 (YYYY-MM-DDTHH:mm:ss+07:00). Diterima: " + input);
        }
    }

    // Helper: Parse tanggal awal (start of day)
    private OffsetDateTime parseIsoDateOrNull(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(input + "T00:00:00+07:00"); // Asumsi WIB
        } catch (Exception e) {
            throw new IllegalArgumentException("Format tanggal harus YYYY-MM-DD");
        }
    }
}
