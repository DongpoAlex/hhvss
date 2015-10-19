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
					系统中目前没有发现您要的单据。
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:for-each select="/xdoc/xout/sheet">
					<xsl:call-template name="output_sheet" />
				</xsl:for-each>
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

	<xsl:template name="output_sheet">
		<xsl:call-template name="output_head" />
		<xsl:call-template name="output_detail" />
	</xsl:template>


	<xsl:template name="output_head">
		<table width="90%" cellspacing="0" cellpadding="1" border="1">
			<tr>
				<td>供应商编码</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/venderid" />
				</td>
				<td>供应商名称</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/vendername" />
				</td>
			</tr>
			<tr>
				<td>缴款通知单单号</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/sheetid" />
				</td>
				<td>收租费用</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(/xdoc/xout/sheet/head/rows/receivableamt,'#0.00')" />
				</td>
			</tr>
			<tr>
				<td>备注</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/note" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_detail">

	</xsl:template>
</xsl:stylesheet>