<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<xsl:if test="/xdoc/xout/xerr/code != 0">
			<xsl:element name="div">
				<xsl:attribute name="class">wrong</xsl:attribute>
				<xsl:value-of select="/xdoc/xout/xerr/note" />
			</xsl:element>
		</xsl:if>
		<xsl:if test="/xdoc/xout/xerr/code = 0">
			<xsl:element name="div">
				<xsl:attribute name="class">ok</xsl:attribute>
				商品验证数据验证通过，可以提交。
			</xsl:element>
		</xsl:if>
		<xsl:call-template name="print_sheet" />
	</xsl:template>
	<xsl:template name="print_sheet">
		<table width="100%" class="tableborder" cellpadding="4" align="center"
			bgcolor="#e3e3e3">
			<tr class="header">
				<td width="5%">装车顺序号</td>
				<td width="10%">商品条码</td>
				<td width="10%">商品规格</td>
				<td width="10%">商品数量</td>
				<td width="10%">外箱码</td>
				<td width="40%">验证结果</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/list/row">
				<xsl:element name="tr">
					<xsl:attribute name="class"><xsl:value-of
						select="message/@class" /></xsl:attribute>
					<td align="right">
						<xsl:value-of select="seqno" />
					</td>
					<td>
						<xsl:value-of select="goodsid" />
					</td>
					<td>
						<xsl:value-of select="spec" />
					</td>
					<td align="right">
						<xsl:value-of select="qty" />
					</td>
					<td>
						<xsl:value-of select="packageid" />
					</td>
					<td>
						<xsl:value-of select="message" />
					</td>
				</xsl:element>
			</xsl:for-each>
			<tr>
				<td>合计：</td>
				<td></td>
				<td></td>
				<td align="right">
					<xsl:value-of select="sum(/xdoc/xout/list/row/qty)"></xsl:value-of>
				</td>
				<td></td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
