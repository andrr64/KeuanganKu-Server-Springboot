package com.andreas.backend.keuanganku.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andreas.backend.keuanganku.SysVar;
import com.andreas.backend.keuanganku.config.TimeConfig;
import com.andreas.backend.keuanganku.dto.CashflowItem;
import com.andreas.backend.keuanganku.dto.SumTransaksiKategori;
import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.KategoriStatistikResponse;
import com.andreas.backend.keuanganku.dto.response.RingkasanBulanIni;
import com.andreas.backend.keuanganku.dto.response.RingkasanTransaksiKategoriResponse;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;
import com.andreas.backend.keuanganku.model.Akun;
import com.andreas.backend.keuanganku.model.Kategori;
import com.andreas.backend.keuanganku.model.Transaksi;
import com.andreas.backend.keuanganku.repository.AkunRepository;
import com.andreas.backend.keuanganku.repository.KategoriRepository;
import com.andreas.backend.keuanganku.repository.TransaksiRepository;
import com.andreas.backend.keuanganku.service.TransaksiService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransaksiServiceImpl implements TransaksiService {

    private final AkunRepository akunRepo;
    private final KategoriRepository kategoriRepo;
    private final TransaksiRepository transaksiRepo;

    // Gunakan konstanta dari TimeConfig
    private static final ZoneOffset SERVER_OFFSET = TimeConfig.SERVER_TIME_ZONE_OFFSET;

    @Override
    public void tambahTransaksi(UUID idPengguna, TransaksiRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request tidak boleh null");
        }

        if (request.getJumlah() == null || request.getJumlah().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0");
        }

        UUID idAkun = UUID.fromString(request.getIdAkun());
        Akun akun = akunRepo.findById(idAkun)
                .filter(a -> a.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Akun tidak ditemukan atau bukan milik pengguna"));

        UUID idKategori = UUID.fromString(request.getIdKategori());
        Kategori kategori = kategoriRepo.findById(idKategori)
                .filter(k -> k.getPengguna() == null || k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak ditemukan atau bukan milik pengguna"));

        OffsetDateTime tanggal = request.getTanggal();

        if (SysVar.isPengeluaran(kategori.getJenis())) {
            if (akun.getSaldo().compareTo(request.getJumlah()) < 0) {
                throw new IllegalArgumentException("Saldo tidak mencukupi untuk pengeluaran (" + akun.getSaldo() + ")");
            }
            akun.setSaldo(akun.getSaldo().subtract(request.getJumlah()));
        } else if (SysVar.isPemasukan(kategori.getJenis())) {
            akun.setSaldo(akun.getSaldo().add(request.getJumlah()));
        } else {
            throw new IllegalArgumentException("Jenis kategori tidak valid");
        }

        akunRepo.save(akun);

        Transaksi transaksi = new Transaksi();
        transaksi.setAkun(akun);
        transaksi.setKategori(kategori);
        transaksi.setJumlah(request.getJumlah());
        transaksi.setTanggal(tanggal); // ✅ waktu asli dari user, dengan offsetnya
        transaksi.setCatatan(request.getCatatan());
        transaksi.setDibuatPada(OffsetDateTime.now(SERVER_OFFSET)); // waktu buat, bebas pakai server
        transaksi.setPengguna(akun.getPengguna());

        transaksiRepo.save(transaksi);
    }

    @Override
    public void updateTransaksi(UUID idPengguna, UUID idTransaksi, TransaksiRequest request) {
        Transaksi transaksiLama = transaksiRepo.findById(idTransaksi)
                .filter(t -> t.getAkun().getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Transaksi tidak ditemukan atau bukan milik pengguna"));

        UUID idKategoriBaru = UUID.fromString(request.getIdKategori());
        Kategori kategoriBaru = kategoriRepo.findById(idKategoriBaru)
                .filter(k -> k.getPengguna() == null || k.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Kategori tidak valid atau bukan milik pengguna"));

        if (kategoriBaru.getJenis() != 1 && kategoriBaru.getJenis() != 2) {
            throw new IllegalArgumentException("Jenis kategori tidak valid. Hanya boleh 1 (pengeluaran) atau 2 (pemasukan)");
        }

        UUID idAkunBaru = UUID.fromString(request.getIdAkun());
        Akun akunBaru = akunRepo.findById(idAkunBaru)
                .filter(a -> a.getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Akun tidak valid atau bukan milik pengguna"));

        OffsetDateTime tanggal = request.getTanggal().withOffsetSameInstant(SERVER_OFFSET);

        Akun akunLama = transaksiLama.getAkun();
        BigDecimal jumlahLama = transaksiLama.getJumlah();
        BigDecimal jumlahBaru = request.getJumlah();

        // Rollback old effect
        if (transaksiLama.getKategori().getJenis() == 1) {
            akunLama.setSaldo(akunLama.getSaldo().add(jumlahLama));
        } else {
            akunLama.setSaldo(akunLama.getSaldo().subtract(jumlahLama));
        }

        // Apply new effect
        if (kategoriBaru.getJenis() == 1) {
            if (akunBaru.getSaldo().compareTo(jumlahBaru) < 0) {
                throw new IllegalArgumentException("Saldo tidak mencukupi untuk pengeluaran");
            }
            akunBaru.setSaldo(akunBaru.getSaldo().subtract(jumlahBaru));
        } else {
            akunBaru.setSaldo(akunBaru.getSaldo().add(jumlahBaru));
        }

        akunRepo.save(akunLama);
        if (!akunLama.getId().equals(akunBaru.getId())) {
            akunRepo.save(akunBaru);
        }

        transaksiLama.setAkun(akunBaru);
        transaksiLama.setKategori(kategoriBaru);
        transaksiLama.setJumlah(jumlahBaru);
        transaksiLama.setTanggal(tanggal);
        transaksiLama.setCatatan(request.getCatatan());

        transaksiRepo.save(transaksiLama);
    }

    @Override
    public void hapusTransaksi(UUID idPengguna, UUID idTransaksi) {
        Transaksi transaksi = transaksiRepo.findById(idTransaksi)
                .filter(t -> t.getAkun().getPengguna().getId().equals(idPengguna))
                .orElseThrow(() -> new EntityNotFoundException("Transaksi tidak ditemukan atau bukan milik pengguna"));

        Akun akun = transaksi.getAkun();
        if (akun == null) {
            throw new EntityNotFoundException("Akun tidak ditemukan");
        }

        Kategori kategori = transaksi.getKategori();
        if (kategori == null) {
            throw new EntityNotFoundException("Kategori tidak ditemukan");
        }

        Integer jenis = kategori.getJenis();
        if (jenis == null) {
            throw new IllegalArgumentException("Jenis kategori tidak boleh null");
        }

        if (transaksi.getJumlah() == null || akun.getSaldo() == null) {
            throw new IllegalStateException("Jumlah transaksi atau saldo akun tidak boleh null");
        }

        switch (jenis) {
            case 1 ->
                akun.setSaldo(akun.getSaldo().add(transaksi.getJumlah()));
            case 2 -> {
                if (akun.getSaldo().compareTo(transaksi.getJumlah()) < 0) {
                    throw new IllegalArgumentException("Saldo tidak mencukupi untuk menghapus transaksi pemasukan ini");
                }
                akun.setSaldo(akun.getSaldo().subtract(transaksi.getJumlah()));
            }
            default ->
                throw new IllegalArgumentException("Jenis kategori tidak valid (harus 1 atau 2)");
        }

        akunRepo.save(akun);
        transaksiRepo.delete(transaksi);
    }

    @Override
    public Page<TransaksiResponse> getFilteredTransaksi(
            UUID idPengguna,
            String keyword,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            Integer jenis,
            UUID idAkun,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                Sort.by("tanggal").descending()
        );

        String formattedKeyword = (keyword != null && !keyword.isBlank()) ? keyword.toLowerCase() : null;

        Page<Transaksi> transaksiPage = transaksiRepo.findFilteredWithSearch(
                idPengguna,
                startDate,
                endDate,
                jenis,
                idAkun,
                formattedKeyword,
                pageable
        );

        return transaksiPage.map(t -> new TransaksiResponse(
                t.getId(),
                t.getAkun().getId(),
                t.getKategori() != null ? t.getKategori().getNama() : "Tanpa Kategori",
                t.getAkun().getNama(),
                t.getKategori() != null ? t.getKategori().getJenis() : 0,
                t.getJumlah(),
                t.getCatatan(),
                t.getTanggal(),
                t.getKategori()
        ));
    }

    @Override
    public List<TransaksiResponse> getRecentTransaksi(UUID idPengguna, int jumlah) {
        Pageable pageable = PageRequest.of(0, jumlah, Sort.by("tanggal").descending());
        Page<Transaksi> page = transaksiRepo.findByPengguna_Id(idPengguna, pageable);
        return page.getContent().stream()
                .map(t -> new TransaksiResponse(
                t.getId(),
                t.getAkun().getId(),
                t.getKategori() != null ? t.getKategori().getNama() : "Tanpa Kategori",
                t.getAkun().getNama(),
                t.getKategori() != null ? t.getKategori().getJenis() : 0,
                t.getJumlah(),
                t.getCatatan(),
                t.getTanggal(),
                t.getKategori()
        ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CashflowItem> getDataGrafikCashflow(UUID idPengguna, int periode) {
        List<CashflowItem> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        switch (periode) {
            case 1 -> {
                // Mingguan (Senin sampai Minggu)
                LocalDate senin = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

                for (int i = 0; i < 7; i++) {
                    LocalDate tanggal = senin.plusDays(i);
                    OffsetDateTime start = tanggal.atStartOfDay().atOffset(SERVER_OFFSET);
                    OffsetDateTime end = tanggal.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);

                    BigDecimal pemasukan = transaksiRepo.getTotalByTanggal(idPengguna, start, end, 2);
                    BigDecimal pengeluaran = transaksiRepo.getTotalByTanggal(idPengguna, start, end, 1);

                    result.add(new CashflowItem(
                            String.valueOf(i + 1), // Index 1-7 (Sen-Min)
                            pemasukan.doubleValue(),
                            pengeluaran.doubleValue()
                    ));
                }
            }
            case 2 -> {
                // Bulanan
                YearMonth currentMonth = YearMonth.from(today);
                int daysInMonth = currentMonth.lengthOfMonth();
                for (int day = 1; day <= daysInMonth; day++) {
                    LocalDate tanggal = today.withDayOfMonth(day);
                    OffsetDateTime start = tanggal.atStartOfDay().atOffset(SERVER_OFFSET);
                    OffsetDateTime end = tanggal.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);

                    BigDecimal pemasukan = transaksiRepo.getTotalByTanggal(idPengguna, start, end, 2);
                    BigDecimal pengeluaran = transaksiRepo.getTotalByTanggal(idPengguna, start, end, 1);

                    result.add(new CashflowItem(
                            String.valueOf(day),
                            pemasukan.doubleValue(),
                            pengeluaran.doubleValue()
                    ));
                }
            }
            default -> {
                // Tahunan
                for (int i = 1; i <= 12; i++) {
                    LocalDate awalBulan = LocalDate.of(today.getYear(), i, 1);
                    LocalDate akhirBulan = awalBulan.withDayOfMonth(awalBulan.lengthOfMonth());

                    OffsetDateTime start = awalBulan.atStartOfDay().atOffset(SERVER_OFFSET);
                    OffsetDateTime end = akhirBulan.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);

                    BigDecimal pemasukan = transaksiRepo.getTotalByTanggal(idPengguna, start, end, 2);
                    BigDecimal pengeluaran = transaksiRepo.getTotalByTanggal(idPengguna, start, end, 1);

                    result.add(new CashflowItem(
                            Month.of(i).name().substring(0, 3),
                            pemasukan.doubleValue(),
                            pengeluaran.doubleValue()
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public RingkasanTransaksiKategoriResponse getDataTransaksiWaktuTertentu(UUID idPengguna, int periode) {
        LocalDate today = LocalDate.now();
        OffsetDateTime startDateTime;
        OffsetDateTime endDateTime;

        switch (periode) {
            case 1 -> {
                // Minggu ini: dari hari Senin sampai sekarang
                LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
                startDateTime = startOfWeek.atStartOfDay().atOffset(SERVER_OFFSET);
                endDateTime = today.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);
            }
            case 2 -> {
                // Bulan ini: dari tanggal 1 sampai akhir bulan
                LocalDate startOfMonth = today.withDayOfMonth(1);
                LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
                startDateTime = startOfMonth.atStartOfDay().atOffset(SERVER_OFFSET);
                endDateTime = endOfMonth.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);
            }
            case 3 -> {
                // Tahun ini: dari Januari sampai Desember
                LocalDate startOfYear = today.withDayOfYear(1);
                LocalDate endOfYear = LocalDate.of(today.getYear(), 12, 31);
                startDateTime = startOfYear.atStartOfDay().atOffset(SERVER_OFFSET);
                endDateTime = endOfYear.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);
            }
            default -> {
                // Hari ini
                startDateTime = today.atStartOfDay().atOffset(SERVER_OFFSET);
                endDateTime = today.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);
            }
        }

        List<Object[]> hasilRepo = transaksiRepo.getRingkasanKategori(idPengguna, startDateTime, endDateTime);
        RingkasanTransaksiKategoriResponse response = new RingkasanTransaksiKategoriResponse();
        for (Object[] row : hasilRepo) {
            String namaKategori = (String) row[0];
            BigDecimal total = (BigDecimal) row[1];
            Integer jenis = (Integer) row[2];
            SumTransaksiKategori item = new SumTransaksiKategori(namaKategori, total);
            if (jenis == 1) {
                response.getPengeluaran().add(item);
            } else if (jenis == 2) {
                response.getPemasukan().add(item);
            }
        }
        Comparator<SumTransaksiKategori> valueComparator = Comparator.comparing(SumTransaksiKategori::getValue).reversed();
        response.getPemasukan().sort(valueComparator);
        response.getPengeluaran().sort(valueComparator);
        return response;
    }

    @Override
    public List<KategoriStatistikResponse> getPengeluaranPerKategoriBulanIni(UUID idPengguna) {
        LocalDate now = LocalDate.now();
        LocalDate awalBulan = now.withDayOfMonth(1);
        OffsetDateTime startDateTime = awalBulan.atStartOfDay().atOffset(SERVER_OFFSET);
        OffsetDateTime endDateTime = now.atTime(LocalTime.MAX).atOffset(SERVER_OFFSET);

        List<Object[]> data = transaksiRepo.getTotalPengeluaranByKategoriBetween(
                idPengguna,
                startDateTime,
                endDateTime
        );

        return data.stream()
                .map(obj -> new KategoriStatistikResponse(
                (String) obj[0],
                ((BigDecimal) obj[1])
        ))
                .sorted((a, b) -> b.getTotalPengeluaran().compareTo(a.getTotalPengeluaran()))
                .collect(Collectors.toList());
    }

    @Override
    public RingkasanBulanIni getRingkasanBulanIni(UUID idPengguna) {
        BigDecimal totalSaldo = transaksiRepo.getTotalSaldo(idPengguna);
        BigDecimal totalPemasukanBulanIni = transaksiRepo.getTotalPemasukanBulanIni(idPengguna);
        BigDecimal totalPengeluaranBulanIni = transaksiRepo.getTotalPengeluaranBulanIni(idPengguna);
        BigDecimal cashflowBulanIni = totalPemasukanBulanIni.subtract(totalPengeluaranBulanIni);
        return new RingkasanBulanIni(
                totalSaldo,
                totalPemasukanBulanIni,
                totalPengeluaranBulanIni,
                cashflowBulanIni
        );
    }

    @Override
    public List<TransaksiResponse> getFilteredTransaksi(UUID idPengguna, Integer jenis, UUID idAkun) {
        List<Transaksi> transaksiList;

        if (idAkun != null) {
            // Jika idAkun diberikan, filter berdasarkan akun
            transaksiList = transaksiRepo.findByPenggunaAndAkun(idPengguna, idAkun);
        } else if (jenis != null) {
            // Jika hanya jenis diberikan, filter berdasarkan jenis
            transaksiList = transaksiRepo.findByPenggunaAndJenis(idPengguna, jenis);
        } else {
            // Jika tidak ada filter, ambil semua transaksi milik pengguna
            transaksiList = transaksiRepo.findByPengguna(idPengguna);
        }

        // Konversi ke DTO Response
        return transaksiList.stream()
                .map(t -> new TransaksiResponse(
                t.getId(),
                t.getAkun().getId(),
                t.getKategori() != null ? t.getKategori().getNama() : "Tanpa Kategori",
                t.getAkun().getNama(),
                t.getKategori() != null ? t.getKategori().getJenis() : 0,
                t.getJumlah(),
                t.getCatatan(),
                t.getTanggal(),
                t.getKategori()
        ))
                .collect(Collectors.toList());
    }
}
