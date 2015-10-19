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
		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>
		<br />
		<xsl:for-each select="body">
			<xsl:call-template name="output_detail1" />
		</xsl:for-each>
		<br />
	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td class="tdHead">单据编号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td class="tdHead">结算主体</td>
				<td>
					<xsl:value-of select="taxername" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">供应商编码</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td class="tdHead">供应商名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">开户银行</td>
				<td>
					<xsl:value-of select="rcvbank" />
				</td>
				<td class="tdHead">银行账号</td>
				<td>
					<xsl:value-of select="rcvaccno" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">支付方式</td>
				<td>
					<xsl:value-of select="paymodeid" />
					<xsl:value-of select="paymodename" />
				</td>
				<td class="tdHead">计划付款日期</td>
				<td>
					<xsl:value-of select="planpaydate" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">付款金额（大写）</td>
				<td>
					<xsl:value-of select="payamtToChinese" />
				</td>
				<td class="tdHead">付款金额（小写）</td>
				<td>
					<xsl:value-of select="format-number(number(payamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">供应商盖章 </td>
				<td colspan="3">（盖章处） </td>
			</tr>
		</table>

	</xsl:template>

	<xsl:template name="output_detail1">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th colspan="11" class="tdHead">对账单据</th>
			</tr>
			<tr>
				<th>结算单号</th>
				<th>计划日期</th>
				<th>结算单应付金额</th>
				<th>本次付款金额</th>
				<th>单据说明</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="pnsheetid" />
					</td>
					<td>
						<xsl:value-of select="planpaydate" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(pnpayamt),'#,##0.00')" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(payamt),'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="remark" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="2">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/pnpayamt),'#,##0.00')" />
				</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/payamt),'#,##0.00')" />
				</td>
				<td colspan="1">
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>