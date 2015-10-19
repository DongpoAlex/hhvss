<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- main start. -->
	<xsl:template match="/">
		<xsl:if test="/xdoc/xout/sheet/charge/@row_count = 0">
			<h3>该结算单没有收据类扣项</h3>
		</xsl:if>
		<xsl:if test="/xdoc/xout/sheet/charge/@row_count > 0">
			<xsl:call-template name="show" />
		</xsl:if>
	</xsl:template>
	<xsl:template name="show">
		<div class='print_head'>
			<div id='print_logo'>
				<xsl:element name="img">
					<xsl:attribute name="src"><xsl:value-of
						select="/xdoc/xout/sheet/@logo" /></xsl:attribute>
				</xsl:element>
			</div>
			<div id='print_title'>
				<xsl:value-of select="/xdoc/xout/sheet/@title" />
			</div>
		</div>
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>BU</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/buname" />
				</td>
				<th>结算主体</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxername" />
				</td>
				<th>结算单号</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/sheetid" />
				</td>
			</tr>
			<tr>
				<th>供应商编码</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/venderid" />
				</td>
				<th>供应商名称</th>
				<td colspan="3">
					<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
				</td>
			</tr>
			<tr>
				<th colspan="2">费用名称</th>
				<th colspan="2">费用金额</th>
				<th colspan="2">备注</th>
			</tr>
			<xsl:for-each select="/xdoc/xout/sheet/charge/row">
				<tr>
					<td colspan="2">
						<xsl:value-of select="chargename" />
					</td>
					<td colspan="2" class="decimal">
						<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
					</td>
					<td colspan="2">
						<xsl:value-of select="remark" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<th>合计人民币（大写）：</th>
				<td colspan="3">
					<xsl:value-of select="/xdoc/xout/sheet/payamtToChinese" />
				</td>
				<th>￥：</th>
				<td>
					<xsl:value-of
						select="format-number(number(sum(/xdoc/xout/sheet/charge/row/chargeamt)),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td>收款单位盖章：</td>
				<td>（盖章处）</td>
				<td colspan="2">制单人：</td>
				<td colspan="2">制单日期： 年 月 日</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
