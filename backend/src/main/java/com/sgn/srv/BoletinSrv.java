package com.sgn.srv;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sgn.dto.Dtos.MateriaRend;
import com.sgn.dto.Dtos.RendRes;
import com.sgn.ent.Usuario;
import com.sgn.repo.InscripcionRepo;
import com.sgn.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
public class BoletinSrv {

    private final NotaSrv notaSrv;
    private final UsuarioRepo usrRepo;
    private final InscripcionRepo inscRepo;

    @Value("${sgn.anio-activo:2026}")
    private int anioAct;

    public BoletinSrv(NotaSrv notaSrv, UsuarioRepo usrRepo, InscripcionRepo inscRepo) {
        this.notaSrv = notaSrv;
        this.usrRepo = usrRepo;
        this.inscRepo = inscRepo;
    }

    public byte[] pdfAlumno(String legajoPedido, Usuario actor) {
        if (actor.getTipo().name().equals("ALUMNO") && !actor.getLegajo().equals(legajoPedido)) {
            throw new AccessDeniedException("No puede descargar el boletín de otro alumno");
        }
        Usuario alum = usrRepo.findById(legajoPedido)
            .orElseThrow(() -> new NoSuchElementException("Alumno no encontrado"));
        var insc = inscRepo.findByAlumnoLegajoAndAnioAndActivaTrue(
    legajoPedido,
    anioAct
);

String curso = insc.isEmpty()
    ? "-"
    : insc.get(0).getCurso().etiqueta();
        RendRes rend = notaSrv.rendimiento(legajoPedido);
        var notas = notaSrv.historialAlumno(legajoPedido);

        try {
            var baos = new ByteArrayOutputStream();
            var doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            var titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
            var sub = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
            var small = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.DARK_GRAY);

            doc.add(new Paragraph("Escuela Ing. E. Latzina — ET35 DE 18", titulo));
            doc.add(new Paragraph("Boletín de notas", sub));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Alumno: " + alum.nomCompleto(), sub));
            doc.add(new Paragraph("Legajo: " + alum.getLegajo()));
            doc.add(new Paragraph("CUIL: " + (alum.getCuil() != null ? alum.getCuil() : "-"), sub));
            doc.add(new Paragraph("Curso: " + curso));
            doc.add(new Paragraph("Año lectivo: " + anioAct, sub));
            doc.add(new Paragraph("Fecha: " + LocalDate.now(), small));
            doc.add(new Paragraph(" "));

            PdfPTable t = new PdfPTable(new float[]{3, 2, 1, 2, 2});
            t.setWidthPercentage(100);
            header(t, "Materia");
            header(t, "Tipo");
            header(t, "Período");
            header(t, "Nota");
            header(t, "Estado");
            for (var n : notas) {
                cell(t, n.materia());
                cell(t, n.tipo());
                cell(t, String.valueOf(n.numPer()));
                cell(t, n.val());
                cell(t, n.est());
            }
            if (notas.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("Sin notas cargadas", small));
                empty.setColspan(5);
                t.addCell(empty);
            }
            doc.add(t);
            doc.add(new Paragraph(" "));
            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el PDF", e);
        }
    }

    private void header(PdfPTable t, String txt) {
        var f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        var c = new PdfPCell(new Phrase(txt, f));
        c.setBackgroundColor(new Color(80, 73, 69));
        c.setPadding(5);
        t.addCell(c);
    }

    private void cell(PdfPTable t, String txt) {
        var f = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        var c = new PdfPCell(new Phrase(txt == null ? "" : txt, f));
        c.setPadding(4);
        t.addCell(c);
    }
}
