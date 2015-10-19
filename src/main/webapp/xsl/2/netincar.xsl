<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:for-each select="/xdoc/xout/sheet">
			<xsl:call-template name="output_sheet" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="output_sheet">
		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>
		<br />
		<xsl:for-each select="body">
			<xsl:call-template name="output_detail1" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td class="tdHead">单据编号</td>
				<td>
					<xsl:value-of select="incarno" />
				</td>
				<td class="tdHead">预约流水号</td>
				<td>
					<xsl:value-of select="order_serial" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">商户编码</td>
				<td>
					<xsl:value-of select="supplier_no" />
				</td>
				<td class="tdHead">商户名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
		</table>

	</xsl:template>

	<xsl:template name="output_detail1">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td colspan="12">装车明细</td>
			</tr>
			<tr>
				<th>序号</th>
				<th>商品条码</th>
				<th>商品名称</th>
				<th>规格</th>
				<th>数量</th>
				<th>外箱码</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="seqno" />
					</td>
					<td>
						<xsl:value-of select="goodsid" />
					</td>
					<td>
						<xsl:value-of select="goodsname" />
					</td>
					<td>
						<xsl:value-of select="spec" />
					</td>
					<td>
						<xsl:value-of select="qty" />
					</td>
					<td>
						<xsl:value-of select="packageid" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>