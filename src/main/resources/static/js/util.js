$(document).ready(function() {
    $('#tabel-pengguna').DataTable();
    $('#tabel-penugasan-periode-ini').DataTable();
    $('#tabel-riwayat-penugasan').DataTable();
    $('#tabel-penilaian-mandiri').DataTable();
    $('#list-karyawan').DataTable();
    // $('#matrix-pmo').DataTable();
    $('#sub-proyek').DataTable();
    $('#table-rekomendasi').DataTable();
    $('#tabel-anggota-divisi').DataTable();
    $('#tabel-finalisasi').DataTable();
    $('#tabel-rf').DataTable();
    $('#tabel-detailkar').DataTable();
    $('#tabel-listproyek').DataTable();
    $('#rekan-seproyek').DataTable();
    $('#divisi-nonaktif').DataTable();
    $('#rekap-proyek').DataTable();
    $('#pmo-proyek').DataTable();
    $('#rekap-riwayat').DataTable( {
        "order": [[ 4, "desc" ]]
    });

    $('.table-mpp').DataTable();

    var table = $('.table-matrix').removeAttr('width').DataTable( {
        scrollY:        "300px",
        scrollX:        "100%",
        scrollCollapse: true,
        fixedColumns:   {
            leftColumns: 1
        },columnDefs: [
            { width: 300, targets: 0 }
        ],
        "order": [[ 1, "desc" ]]
    } );

    $('.notifModal').modal('show');

    $(".edit-cuti-btn").on("click", function(){
        event.preventDefault();

        // get data from item penilaian mandiri
        var jmlHari = $(this).data('haricuti');
        var tanggalMulai = $(this).data('tanggalmulai');
        var tanggalSelesai = $(this).data('tanggalselesai');
        var idKaryawan = $(this).data('idkar');
        var idCuti = $(this).data('id');

        // fill form value and action
        $("#jmlHari").attr('value', jmlHari);
        $("#tgl-mulai").attr('value', tanggalMulai);
        $("#tgl-selesai").attr('value', tanggalSelesai);
        $("#idKaryawan").attr('value', idKaryawan);
        $("#idCuti").attr('value', idCuti);

        // pop-up modal
        $("#editCutiModal").modal();
    })

    $('#feedbackModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var header = button.data('title'); // Extract info from data-* attributes
        var feedback = button.data('feedback');
        var rating = button.data('rating');
        var namaRekan = button.data('namarekan');
        var kodeProyek = button.data('kodeproyek');
        var idRekan = button.data('idrekan');

        var modal = $(this);
        if(feedback != 'Belum memberikan Feedback'){
            modal.find('.modal-body textarea').val(feedback);
        }else{
            modal.find('.modal-body textarea').val("");
        }

        if(feedback != 0){
            modal.find('.modal-body #rating').val(rating);
        }else {
            modal.find('.modal-body #rating').val(0);
        }


        modal.find('.modal-body #namaRekan').val(namaRekan);
        modal.find('.modal-body #idRekan').val(idRekan);
        modal.find('.modal-body #kodeProyek').val(kodeProyek);

    });

    $('#checkoutModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var header = button.data('title'); // Extract info from data-* attributes
        var waktuCheckin = button.data('waktu-checkin');

        var modal = $(this);
        modal.find('.modal-body textarea').val("");


        modal.find('.modal-body #waktuCheckin').val(waktuCheckin);

    });

    $(".btn-isi-evaluasi-karyawan").on("click", function(){
        event.preventDefault();

        // get data from item penilaian mandiri
        var periode = $(this).data('periode');
        var isiRekap = $(this).data('rekap');
        var id = $(this).data('id');
        var proyek = $(this).data('proyek');
        var action = "/assignment/karyawan/penilaian-mandiri/tambah/" + proyek + "/" + id;

        // generate date object with periode
        dateStr = periode.split("-");
        periode = new Date(dateStr[0], dateStr[1] - 1, dateStr[2]);

        // generate deadline with periode + 1 month
        var deadline = new Date(periode.getTime());
        deadline.setMonth(periode.getMonth()+1);
        const monthNames = ["JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
            ];
        
        
        var periodeString = (monthNames[periode.getMonth()]+" "+periode.getFullYear());
        var deadlineString = (monthNames[deadline.getMonth()]+" "+deadline.getFullYear());
        
        // fill form value and action
        $("#periode-penugasan").attr('value', periodeString);
        $("#deadline-pengisian").attr('value', deadlineString);
        $("#isi-evaluasi").val(isiRekap);
        $("#form-isi-evaluasi-karyawan").attr('action', action);
        
        // pop-up modal
        $("#modal-isi-ubah-evaluasi-karyawan").modal();
    })



    $(".pusil-btn-nonactive").on("click", function(){
        event.preventDefault();
        var action = "/employee/admin/keloladivisi/nonaktifkan/"  + $(this).data('id');

        $("#btn-konfirmasi-nonaktifkan-divisi").attr('href', action);
        $("#modal-nonaktifkan-divisi").modal();
    })

    $(".pusil-btn-active").on("click", function(){
        event.preventDefault();
        var action = "/employee/admin/keloladivisi/aktifkan/"  + $(this).data('idnonaktif');

        $("#btn-konfirmasi-aktifkan-divisi").attr('href', action);
        $("#modal-aktifkan-divisi").modal();
    })

    $(".pusil-btn-delete").on("click", function(){
        event.preventDefault();
        var action = "/employee/admin/keloladivisi/hapus/"  + $(this).data('id');

        $("#btn-konfirmasi-hapus-divisi").attr('href', action);
        $("#modal-hapus-divisi").modal();
    })

    $(".btn-hapus-karyawan").on("click", function(){
        event.preventDefault();
        var action = "/employee/hr/hapus/" + $(this).data('idkaryawan');
        $("#btn-konfirmasi-hapus-karyawan").attr('href', action);
        $("#modal-hapus-karyawan").modal();
    })

    $('[data-toggle="tooltip"]').tooltip();


    $(".btn-update-gaji").on("click",function(){
        var id = $(this).data('idgaji');
        var idKaryawan = $(this).data("idkaryawan")
        var gaji = $(this).data("gaji")
        var action = "/employee/detail-karyawan/" + idKaryawan + "/update-gaji/" + id;
        $("#input-gaji").attr("value", gaji)
        $("#form-update-gaji").attr('action' , action);
        $("#modal-update-gaji").modal();
    });

    $(".btn-delete-gaji").on("click",function(){
        var id = $(this).data('idgaji');
        var idKaryawan = $(this).data("idkaryawan")
        var action = "/employee/detail-karyawan/" + idKaryawan + "/delete-gaji/" + id;
        $("#form-delete-gaji").attr('action' , action);
        $("#modal-delete-gaji").modal();
    });

    $(".toogle-status-karyawan").on("click",function(){
        var statusKaryawan = $(this).data("status");
        var idKaryawan = $(this).data("idkaryawan")
        var textBtn;
        var textKonfirmasi;
        var action;
        if(statusKaryawan == "aktif"){
            textBtn = "Nonaktifkan";
            textKonfirmasi = "Apakah anda ingin menonaktifkan anggota ini?";
            action = "/employee/detail-karyawan/"+idKaryawan+"/deactivate";
        }else{
            textBtn = "Aktifkan";
            textKonfirmasi = "Apakah anda ingin mengaktifkan anggota ini?"
            action = "/employee/detail-karyawan/"+idKaryawan+"/activate";
        }
        $("#text-konfirmasi-status").text(textKonfirmasi);
        $("#ubah-konfirmasi-status").text(textBtn);
        $("#form-update-status").attr('action' , action);
        $("#modal-update-status").modal();
    });
} );