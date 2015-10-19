package com.royalstone.workbook;

import jxl.HeaderFooter;
import jxl.SheetSettings;
import jxl.format.PageOrientation;
import jxl.format.PaperSize;
import jxl.write.WritableSheet;

/**
 * * 读取Jxl方法并设置 *
 * 
 * @author 邱大为 *
 * @version 1.0
 */
public class TestPrint {
	/**
	 * * 该方法将setting设置到sheet中 *
	 * 
	 * @param sheet
	 *            需要设置的sheet *
	 * @param setting
	 *            被设置的属性 *
	 * @return
	 */
	public WritableSheet copySheetSettingToSheet( WritableSheet sheet, SheetSettings setting ) { // 设置原Sheet打印属性到新Sheet页
		SheetSettings sheetSettings = sheet.getSettings();
//		sheetSettings.setAutomaticFormulaCalculation(setting.getAutomaticFormulaCalculation());
		sheetSettings.setBottomMargin(setting.getBottomMargin());
		sheetSettings.setCopies(setting.getCopies());
		sheetSettings.setDefaultColumnWidth(setting.getDefaultColumnWidth());
		sheetSettings.setDefaultRowHeight(setting.getDefaultRowHeight());
		sheetSettings.setDisplayZeroValues(setting.getDisplayZeroValues());
		sheetSettings.setFitHeight(setting.getFitHeight());
		sheetSettings.setFitToPages(setting.getFitToPages());
		sheetSettings.setFitWidth(setting.getFitWidth());
		HeaderFooter footer = setting.getFooter();
		if (footer != null) {
			sheetSettings.setFooter(footer);
		}
		sheetSettings.setFooterMargin(setting.getFooterMargin());
		HeaderFooter header = setting.getHeader();
		if (header != null) {
			sheetSettings.setHeader(header);
		}
		sheetSettings.setHeaderMargin(setting.getHeaderMargin());
		sheetSettings.setHidden(setting.isHidden());
		sheetSettings.setHorizontalCentre(setting.isHorizontalCentre());
		sheetSettings.setHorizontalFreeze(setting.getHorizontalFreeze());
		sheetSettings.setHorizontalPrintResolution(setting.getHorizontalPrintResolution());
		sheetSettings.setLeftMargin(setting.getLeftMargin());
//		sheetSettings.setNormalMagnification(setting.getNormalMagnification());
		PageOrientation pageOrientation = setting.getOrientation();
		if (pageOrientation != null) {
			sheetSettings.setOrientation(pageOrientation);
		}
//		sheetSettings.setPageBreakPreviewMagnification(setting.getPageBreakPreviewMagnification());
//		sheetSettings.setPageBreakPreviewMode(setting.getPageBreakPreviewMode());
		sheetSettings.setPageStart(setting.getPageStart());
		PaperSize paperSize = setting.getPaperSize();
		if (paperSize != null) {
			sheetSettings.setPaperSize(setting.getPaperSize());
		}
		sheetSettings.setPassword(setting.getPassword());
		sheetSettings.setPasswordHash(setting.getPasswordHash());
//		Range printArea = setting.getPrintArea();
//		if (printArea != null) {
//			sheetSettings.setPrintArea(printArea.getTopLeft() == null ? 0 : printArea.getTopLeft().getColumn(),
//					printArea.getTopLeft() == null ? 0 : printArea.getTopLeft().getRow(),
//					printArea.getBottomRight() == null ? 0 : printArea.getBottomRight().getColumn(), printArea
//							.getBottomRight() == null ? 0 : printArea.getBottomRight().getRow());
//		}
		sheetSettings.setPrintGridLines(setting.getPrintGridLines());
		sheetSettings.setPrintHeaders(setting.getPrintHeaders());
//		Range printTitlesCol = setting.getPrintTitlesCol();
//		if (printTitlesCol != null) {
//			sheetSettings.setPrintTitlesCol(printTitlesCol.getTopLeft() == null ? 0 : printTitlesCol.getTopLeft()
//					.getColumn(), printTitlesCol.getBottomRight() == null ? 0 : printTitlesCol.getBottomRight()
//					.getColumn());
//		}
//		Range printTitlesRow = setting.getPrintTitlesRow();
//		if (printTitlesRow != null) {
//			sheetSettings.setPrintTitlesRow(printTitlesRow.getTopLeft() == null ? 0 : printTitlesRow.getTopLeft()
//					.getRow(), printTitlesRow.getBottomRight() == null ? 0 : printTitlesRow.getBottomRight().getRow());
//		}
		sheetSettings.setProtected(setting.isProtected());
//		sheetSettings.setRecalculateFormulasBeforeSave(setting.getRecalculateFormulasBeforeSave());
		sheetSettings.setRightMargin(setting.getRightMargin());
		sheetSettings.setScaleFactor(setting.getScaleFactor());
		sheetSettings.setSelected(setting.isSelected());
		sheetSettings.setShowGridLines(setting.getShowGridLines());
		sheetSettings.setTopMargin(setting.getTopMargin());
		sheetSettings.setVerticalCentre(setting.isVerticalCentre());
		sheetSettings.setVerticalFreeze(setting.getVerticalFreeze());
		sheetSettings.setVerticalPrintResolution(setting.getVerticalPrintResolution());
		sheetSettings.setZoomFactor(setting.getZoomFactor());
		return sheet;
	}
}
