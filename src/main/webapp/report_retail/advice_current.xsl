<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<xsl:call-template name="show_sheetlist" />
	</xsl:template>

	<xsl:template name="show_sheetlist">
		<xsl:element name="table">
			<xsl:attribute name="border">1</xsl:attribute>
			<xsl:attribute name="width">50%</xsl:attribute>
			<xsl:attribute name="cellspadding">3</xsl:attribute>
			<xsl:attribute name="cellSpacing">1</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					所属分公司
				</xsl:element>
				<!-- <xsl:element name="th"> <xsl:attribute name="align">center</xsl:attribute> 
					上月结余 </xsl:element> <xsl:element name="th"> <xsl:attribute name="align">center</xsl:attribute> 
					本月增加 </xsl:element> <xsl:element name="th"> <xsl:attribute name="align">center</xsl:attribute> 
					本月付款 </xsl:element> -->
				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					金额
				</xsl:element>
			</xsl:element>

			<xsl:for-each select="/xdoc/xout/report/row">
				<xsl:sort select="accmonth" order="ascending" data-type="text" />
				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="bookname" />
					</xsl:element>
					<!-- <xsl:element name="td"> <xsl:attribute name="align">right</xsl:attribute> 
						<xsl:value-of select="format-number( (openadviceamt+opentaxamt),'#,##0.00')" 
						/> </xsl:element> <xsl:element name="td"> <xsl:attribute name="align">right</xsl:attribute> 
						<xsl:value-of select="format-number( (incadviceamt+inctaxamt),'#,##0.00')" 
						/> </xsl:element> <xsl:element name="td"> <xsl:attribute name="align">right</xsl:attribute> 
						<xsl:value-of select="format-number( (decadviceamt+dectaxamt),'#,##0.00')" 
						/> </xsl:element> -->
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number( (adviceamt+taxamt),'#,##0.00')" />
					</xsl:element>

				</xsl:element>
			</xsl:for-each>

		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
