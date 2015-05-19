package gui;

import domain.Loan;
import domain.User;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import persistence.LoanRepository;
import persistence.UserRepository;

/**
 * A Util-class to export the current state of the system.
 *
 * @author Frederik
 */
public class PdfExporter {

    private static final String USER_FILE_NAME = "Gebruikers";
    private static final String LOAN_FILE_NAME = "Uitleningen";
    private static final String ITEMS_FILE_NAME = "Voorwerpen";
    private static final PDFont FONT = PDType1Font.HELVETICA;
    private static final int X_OFFSET = 50;
    private static final int Y_OFFSET = 50;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static void saveUsers() throws IOException {
	final int stepY = 50;
	PDDocument document = new PDDocument();
	List<User> users = UserRepository.getInstance().getUsersByPredicate(u -> u.getUserType() == User.UserType.STUDENT);
	float y = 0;
	PDPage page;
	PDRectangle rectangle;
	PDPageContentStream cos;

	page = new PDPage(PDPage.PAGE_SIZE_A4);
	document.addPage(page);
	rectangle = page.getMediaBox();
	cos = new PDPageContentStream(document, page);

	cos.beginText();
	cos.moveTextPositionByAmount(X_OFFSET, rectangle.getHeight() - Y_OFFSET - y);
	cos.setFont(FONT, 21);
	cos.drawString("Overzicht van gebruikers");
	y += 40;
	cos.endText();

	for (User user : users) {
	    if (user.getName() == null || user.getName().isEmpty() || user.getName().equals("null")) {
		continue;
	    }

	    if (rectangle.getHeight() - y <= 100) {
		cos.close();
		page = new PDPage(PDPage.PAGE_SIZE_A4);
		document.addPage(page);
		rectangle = page.getMediaBox();
		cos = new PDPageContentStream(document, page);
		y = 0;
	    }

	    System.out.println(rectangle.getHeight() - y);
	    cos.beginText();
	    cos.moveTextPositionByAmount(X_OFFSET, rectangle.getHeight() - Y_OFFSET - y);
	    cos.setFont(FONT, 13);
	    if (user.getName() != null) {
		cos.drawString(user.getName() + ", " + user.getClassRoom() + (user.getRegisterNumber() == null || user.getRegisterNumber().isEmpty() ? "" : ", " + user.getRegisterNumber()));
	    }

	    y += stepY;
	    cos.endText();
	}

	cos.close();

	try {
	    document.save(System.getProperty("user.home") + "/" + USER_FILE_NAME + " op " + DATE_FORMAT.format(Date.from(Instant.now())) + ".pdf");
	} catch (COSVisitorException ex) {
	    Logger.getLogger(PdfExporter.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
	    document.close();
	}
    }

    public static void saveLoans() throws IOException {
	final int stepY = 50;
	PDDocument document = new PDDocument();
	List<Loan> loans = LoanRepository.getInstance().getLoans().stream().filter(l -> !l.getReturned()).collect(Collectors.toList());
	float y = 0;
	PDPage page;
	PDRectangle rectangle;
	PDPageContentStream cos;

	page = new PDPage(PDPage.PAGE_SIZE_A4);
	document.addPage(page);
	rectangle = page.getMediaBox();
	cos = new PDPageContentStream(document, page);

	cos.beginText();
	cos.moveTextPositionByAmount(X_OFFSET, rectangle.getHeight() - Y_OFFSET - y);
	cos.setFont(FONT, 21);
	cos.drawString("Overzicht van open uitleningen");
	y += 40;
	cos.endText();

	for (Loan loan : loans) {
	    if (rectangle.getHeight() - y <= 100) {
		cos.close();
		page = new PDPage(PDPage.PAGE_SIZE_A4);
		document.addPage(page);
		rectangle = page.getMediaBox();
		cos = new PDPageContentStream(document, page);
		y = 0;
	    }

	    System.out.println(rectangle.getHeight() - y);
	    cos.beginText();
	    cos.moveTextPositionByAmount(X_OFFSET, rectangle.getHeight() - Y_OFFSET - y);
	    cos.setFont(FONT, 13);
	    cos.drawString(loan + " van " + DATE_FORMAT.format(loan.getStartDate()) + DATE_FORMAT.format(loan.getDate()));

	    y += stepY;
	    cos.endText();
	}

	cos.close();

	try {
	    document.save(System.getProperty("user.home") + "/" + LOAN_FILE_NAME + " op " + DATE_FORMAT.format(Date.from(Instant.now())) + ".pdf");
	} catch (COSVisitorException ex) {
	    Logger.getLogger(PdfExporter.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
	    document.close();
	}
    }
}
