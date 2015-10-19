<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code = 100">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					系统中目前没有您要的发票信息。
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:for-each select="/xdoc/xout/paymentnotedtl">
					<xsl:call-template name="output_invoice" />
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=-1">
				<xsl:element name="div">
					<xsl:attribute name="class">warning</xsl:attribute>
					在此付款申请单内没有提供发票信息.
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:attribute name="class">warning</xsl:attribute>
					<xsl:value-of select="xdoc/xerr/code" />
					:
					<xsl:value-of select="xdoc/xerr/note" />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="output_invoice">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">1</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>
			<br />
			<br />
			发票清单:
			<br />
			<br />
			<xsl:element name="tr">
				<xsl:element name="th">
					发票号
				</xsl:element>
				<xsl:element name="th">
					发票类型
				</xsl:element>
				<xsl:element name="th">
					开票日期
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="align">right</xsl:attribute>
					税率
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="align">right</xsl:attribute>
					税额
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="align">right</xsl:attribute>
					价额
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="align">right</xsl:attribute>
					发票说明
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="rows">
				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:value-of select="invoiceno" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="invoicetype" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="invoicedate" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of select="format-number(taxrate,'#,##0.00')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of select="format-number(taxamt,'#,##0.00')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of select="format-number(taxableamt,'#,##0.00')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="goodsdesc" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>
