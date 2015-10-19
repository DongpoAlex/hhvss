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
		<xsl:for-each select="item">
			<xsl:call-template name="output_detail1" />
		</xsl:for-each>
		<br />
		<xsl:for-each select="charge_tax">
			<xsl:call-template name="output_detail2" />
		</xsl:for-each>
		<br />
		<xsl:for-each select="charge_notax">
			<xsl:call-template name="output_detail3" />
		</xsl:for-each>
		<br />
		<xsl:for-each select="invoice">
			<xsl:call-template name="output_detail4" />
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td class="tdHead" width="20%">单据编号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td class="tdHead" width="20%">BU</td>
				<td>
					<xsl:value-of select="buname" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">供应商编码</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td class="tdHead">结算主体</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxername" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">支付方式</td>
				<td>
					<xsl:value-of select="paymodename" />
				</td>
				<td class="tdHead">供应商名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">开户银行</td>
				<td>
					<xsl:value-of select="bankbranchname" />
				</td>
				<td class="tdHead">银行账号</td>
				<td>
					<xsl:value-of select="bankaccount" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">本期对账金额</td>
				<td>
					<xsl:value-of select="format-number(number(payableamt),'#,##0.00')" />
				</td>
				<td class="tdHead">计划付款日期</td>
				<td>
					<xsl:value-of select="planpaydate" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">17%税建议开票金额</td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt17),'#,##0.00')" />
				</td>
				<td class="tdHead">13%税建议开票金额 </td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt13),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">0%税建议开票金额</td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt0),'#,##0.00')" />
				</td>
				<td class="tdHead">预付款冲抵金额</td>
				<td>
					<xsl:value-of select="format-number(number(suspayamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">扣项一合计金额</td>
				<td>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/charge_tax/row/chargeamt),'#,##0.00')" />
				</td>
				<td class="tdHead">扣项二合计金额</td>
				<td>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/charge_notax/row/chargeamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">本期实付金额（大写）</td>
				<td>
					<xsl:value-of select="payamtToChinese" />
				</td>
				<td class="tdHead">本期实付金额（小写） </td>
				<td>
					<xsl:value-of select="format-number(number(payamt),'#,##0.00')" />
				</td>
			</tr>
		</table>
		<table border="0" cellspacing="0" cellpadding="4">
			<tr>
				<td class="" style="border-right:1px solid #000" width="20%">开票信息</td>
				<td colspan="3">
					开票名称：
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxername" />
					<br />
					纳税人识别号：
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxpayerid" />
					<br />
					地址、电话：
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxeradress" />
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxertelno" />
					<br />
					开户行及帐号：
					<xsl:value-of select="/xdoc/xout/sheet/head/row/bankid" />
					<xsl:value-of select="/xdoc/xout/sheet/head/row/cbankaccount" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_detail1">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th colspan="11" class="tdHead">对账单据</th>
			</tr>
			<tr>
				<th>单号</th>
				<th>单据类型</th>
				<th>门店编码</th>
				<th>门店名称</th>
				<th>对账金额</th>
				<th>物流模式</th>
				<th>单据发生日期</th>
				<th>结算方式</th>
				<th>课类信息</th>
				<th>单据说明</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="docno" />
					</td>
					<td>
						<xsl:value-of select="sheettypename" />
					</td>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(totalpayamt),'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="logisticsname" />
					</td>
					<td>
						<xsl:value-of select="docdate" />
					</td>
					<td>
						<xsl:value-of select="paytypename" />
					</td>
					<td>
						<xsl:value-of select="categoryid" />
						<xsl:value-of select="categoryname" />
					</td>
					<td>
						<xsl:value-of select="remark" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="4">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/item/row/totalpayamt),'#,##0.00')" />
				</td>
				<td colspan="5">
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_detail2">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th colspan="6" class="tdHead">扣项一(票扣明细) </th>
			</tr>
			<tr>
				<th>扣项单号</th>
				<th>门店编号</th>
				<th>门店名称</th>
				<th>扣项名称</th>
				<th>扣项金额</th>
				<th>扣项说明</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="noteno" />
					</td>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="remark" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="4">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/charge_tax/row/chargeamt),'#,##0.00')" />
				</td>
				<td colspan="1">
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_detail3">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th colspan="6" class="tdHead">扣项二(非票扣明细) </th>
			</tr>
			<tr>
				<th>扣项单号</th>
				<th>门店编号</th>
				<th>门店名称</th>
				<th>扣项名称</th>
				<th>扣项金额</th>
				<th>扣项说明</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="noteno" />
					</td>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="remark" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="4">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/charge_notax/row/chargeamt),'#,##0.00')" />
				</td>
				<td colspan="1">
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_detail4">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th colspan="8" class="tdHead">发票清单 </th>
			</tr>
			<tr>
				<th>发票号码</th>
				<th>发票类型</th>
				<th>发票种类</th>
				<th>开票日期</th>
				<th>税率</th>
				<th>税额</th>
				<th>价额</th>
				<th>价税合计</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="invoiceno" />
					</td>
					<td>
						<xsl:value-of select="invoicecode" />
					</td>
					<td>
						<xsl:value-of select="invoicetypename" />
					</td>
					<td>
						<xsl:value-of select="invoicedate" />
					</td>
					<td>
						<xsl:value-of select="taxrate" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(taxamt),'#,##0.00')" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(taxableamt),'#,##0.00')" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(totalamt),'#,##0.00')" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>