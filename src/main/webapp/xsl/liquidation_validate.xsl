<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<xsl:if test="/xdoc/xout/rowset/result != 'OK'">
			<xsl:element name="div">
				<xsl:attribute name="class">titlewarning</xsl:attribute>
				验证失败:
				<xsl:value-of select="/xdoc/xout/rowset/result" />
			</xsl:element>
		</xsl:if>
		<xsl:if test="/xdoc/xout/rowset/result = 'OK'">
			<xsl:element name="div">
				<xsl:attribute name="class">ok</xsl:attribute>
				数据验证通过!请确认数据无误后单击提交按钮
			</xsl:element>
		</xsl:if>
		<xsl:call-template name="print_sheet" />
	</xsl:template>
	<xsl:template name="print_sheet">
		<table width="100%" class="tablebg" cellpadding="3" cellspacing="1">
			<tr class="header">
				<th>序号</th>
				<th>BU</th>
				<th>单据类型</th>
				<th>单据编码</th>
				<th>对账金额</th>
				<th>单据日期</th>
				<th>验证结果</th>
			</tr>
			<xsl:for-each select="xdoc/xout/rowset/row">
				<xsl:element name="tr">
					<xsl:attribute name="class"><xsl:value-of
						select="@error" /></xsl:attribute>
					<td>
						<xsl:value-of select="seqno" />
					</td>
					<td>
						<xsl:value-of select="buid" />
						<xsl:value-of select="buname" />
					</td>
					<td>
						<xsl:value-of select="sheettype" />
					</td>
					<td>
						<xsl:value-of select="sheetid" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(unpaidamt,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="docdate" />
					</td>
					<td>
						<xsl:value-of select="note" />
					</td>
				</xsl:element>
			</xsl:for-each>
			<tr>
				<td>合计：</td>
				<td></td>
				<td></td>
				<td></td>
				<td align="right" id="uploadamt">
					<xsl:value-of
						select="format-number( sum(xdoc/xout/rowset/row/unpaidamt),'#,##0.00' )"></xsl:value-of>
				</td>
				<td></td>
				<td></td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
