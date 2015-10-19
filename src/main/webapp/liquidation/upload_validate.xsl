<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<xsl:if test="/xdoc/xerr/code != 0">
			<xsl:element name="div">
				<xsl:attribute name="class">wrong</xsl:attribute>
				<xsl:value-of select="/xdoc/xerr/note" />
			</xsl:element>
		</xsl:if>
		<xsl:if test="/xdoc/xerr/code = 0">
			<xsl:element name="div">
				<xsl:attribute name="class">ok</xsl:attribute>
				数据验证通过，可以提交
			</xsl:element>
		</xsl:if>
		<xsl:call-template name="print_sheet" />
	</xsl:template>
	<xsl:template name="print_sheet">
		<table width="100%" class="tableborder" cellpadding="4" align="center"
			bgcolor="#e3e3e3">
			<tr class="header">
				<td width="5%">序号</td>
				<td width="15%">单据号</td>
				<td width="10%">金额</td>
				<td width="10%">审核日期</td>
				<td width="45%">验证结果</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/list/row">
				<xsl:element name="tr">
					<xsl:attribute name="class"><xsl:value-of
						select="message/@class" /></xsl:attribute>
					<td align="center">
						<xsl:value-of select="seqno" />
					</td>
					<td>
						<xsl:value-of select="noteno" />
					</td>
					<td align="right">
						<xsl:value-of select="format-number(notevalue,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="docdate" />
					</td>
					<td>
						<xsl:value-of select="message" />
					</td>
				</xsl:element>
			</xsl:for-each>
			<tr>
				<td>合计：</td>
				<td></td>
				<td align="right">
					<xsl:value-of
						select="format-number( sum(/xdoc/xout/list/row/notevalue),'#,##0.00' )"></xsl:value-of>
				</td>
				<td></td>
				<td></td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
