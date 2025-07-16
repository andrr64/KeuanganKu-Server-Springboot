package com.andreas.backend.keuanganku.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@Transactional
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepo;
    private final AkunRepository akunRepo;
    private final PenggunaRepository penggunaRepo;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        LocalDateTime tanggal;
        try {
            tanggal = LocalDateTime.parse(req.getTanggal(), dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal tidak valid, gunakan dd/MM/yyyy HH:mm");
        }

        // update saldo
        dari.setSaldo(dari.getSaldo().subtract(req.getJumlah()));
        ke.setSaldo(ke.getSaldo().add(req.getJumlah()));
        akunRepo.saveAll(List.of(dari, ke));

        Transfer transfer = new Transfer();
        transfer.setPengguna(penggunaRepo.findById(idPengguna).orElseThrow(() -> new EntityNotFoundException("Pengguna tidak ditemukan")));
        transfer.setDariAkun(dari);
        transfer.setKeAkun(ke);
        transfer.setJumlah(req.getJumlah());
        transfer.setTanggal(tanggal);
        transfer.setCatatan(req.getCatatan());

        return toResponse(transferRepo.save(transfer));
    }

    @Override
    public List<TransferResponse> getAllTransfer(UUID idPengguna, UUID idAkun, String startDate, String endDate, String sort) {
        LocalDate start = parseDateOrNull(startDate);
        LocalDate end = parseDateOrNull(endDate);

        List<Transfer> all = transferRepo.findAllByPengguna_Id(idPengguna);
        List<Transfer> hasil = new ArrayList<>();

        for (Transfer t : all) {
            if (idAkun != null
                    && !t.getDariAkun().getId().equals(idAkun)
                    && !t.getKeAkun().getId().equals(idAkun)) {
                continue;
            }

            if (start != null && t.getTanggal().toLocalDate().isBefore(start)) {
                continue;
            }
            if (end != null && t.getTanggal().toLocalDate().isAfter(end)) {
                continue;
            }

            hasil.add(t);
        }

        hasil.sort((a, b) -> "asc".equalsIgnoreCase(sort) ? a.getTanggal().compareTo(b.getTanggal()) : b.getTanggal().compareTo(a.getTanggal()));

        List<TransferResponse> res = new ArrayList<>();
        for (Transfer t : hasil) {
            res.add(toResponse(t));
        }
        return res;
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

        // Cek kepemilikan transfer
        if (!t.getPengguna().getId().equals(idPengguna)) {
            throw new IllegalArgumentException("Anda tidak memiliki akses ke transfer ini");
        }

        Akun akunDari = akunRepo.findById(t.getDariAkun().getId())
                .orElseThrow(() -> new EntityNotFoundException("Akun pengirim tidak ditemukan"));

        Akun akunKe = akunRepo.findById(t.getKeAkun().getId())
                .orElseThrow(() -> new EntityNotFoundException("Akun penerima tidak ditemukan"));

        BigDecimal jumlah = t.getJumlah();

        // Cek apakah saldo akun penerima cukup untuk dikurangi
        if (akunKe.getSaldo().compareTo(jumlah) < 0) {
            throw new IllegalArgumentException("Saldo akun penerima tidak mencukupi untuk rollback transfer");
        }

        // Rollback saldo
        akunDari.setSaldo(akunDari.getSaldo().add(jumlah));
        akunKe.setSaldo(akunKe.getSaldo().subtract(jumlah));

        akunRepo.save(akunDari);
        akunRepo.save(akunKe);

        // Hapus transfer
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
            try {
                LocalDateTime tanggalBaru = LocalDateTime.parse(request.getTanggal(), dateTimeFormatter);
                transfer.setTanggal(tanggalBaru);
                isUpdated = true;
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Format tanggal harus dd/MM/yyyy HH:mm");
            }
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

    private LocalDate parseDateOrNull(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(input, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format tanggal harus dd/MM/yyyy");
        }
    }
}
