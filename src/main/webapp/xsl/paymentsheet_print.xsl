<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:for-each select="/xdoc/xout/sheet">
			<xsl:if test="/xdoc/xout/sheet/head/row/flag=2">
				<xsl:call-template name="output_sheet" />
			</xsl:if>
			<xsl:if test="/xdoc/xout/sheet/head/row/flag!=2">
				<h2>仅制单审核状态可打印</h2>
			</xsl:if>
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

		<xsl:if test="/xdoc/xout/sheet/@showType = 'detail'">
			<xsl:for-each select="head/row">
				<xsl:call-template name="output_head" />
			</xsl:for-each>
			<xsl:for-each select="item">
				<xsl:call-template name="output_detail1" />
			</xsl:for-each>
			<xsl:for-each select="charge_tax">
				<xsl:call-template name="output_detail2" />
			</xsl:for-each>
			<xsl:for-each select="charge_notax">
				<xsl:call-template name="output_detail3" />
			</xsl:for-each>
		</xsl:if>

		<xsl:if test="/xdoc/xout/sheet/@showType = 'group'">
			<xsl:for-each select="head/row">
				<xsl:call-template name="output_head" />
			</xsl:for-each>
			<xsl:for-each select="groupitem">
				<xsl:call-template name="output_detail11" />
			</xsl:for-each>
			<xsl:for-each select="group_charge_tax">
				<xsl:call-template name="output_detail21" />
			</xsl:for-each>
			<xsl:for-each select="group_charge_notax">
				<xsl:call-template name="output_detail31" />
			</xsl:for-each>
		</xsl:if>

		<table>
			<tr>
				<td class="nowrap">本期实付金额(大写)</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/payamtToChinese" />
				</td>
				<td class="nowrap">本期实付金额(小写)</td>
				<td>
					<xsl:value-of
						select=" format-number(number(/xdoc/xout/sheet/head/row/payamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="nowrap">供应商盖章 </td>
				<td colspan="3">（盖章处） </td>
			</tr>
			<tr>
				<td class="nowrap">备注 </td>
				<td colspan="3">
					<xsl:value-of select="/xdoc/xout/sheet/head/row/note" />
				</td>
			</tr>
			<tr>
				<td class="nowrap">注意</td>
				<td colspan="3">
					1、打印供应商对账单必须设置页码，每页对账单必须加盖公章或财务专用章。
					<br />
					2、如果供应商在计划付款日期之后或者之前的7天内交票，我公司付款日期将按交票日+7天逢周五付款进行处理。
					<br />
				</td>
			</tr>
			<tr>
				<td class="">制单人：</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editor" />
				</td>
				<td class="">制单日期： </td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editdate" />
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_head">
		<table>
			<tr>
				<td class="" width="20%">单据编号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td class="" width="20%">BU</td>
				<td>
					<xsl:value-of select="buname" />
				</td>
			</tr>
			<tr>
				<td class="">供应商编码</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td class="">结算主体</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxername" />
				</td>
			</tr>
			<tr>
				<td class="">支付方式</td>
				<td>
					<xsl:value-of select="paymodename" />
				</td>
				<td class="">供应商名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
			<tr>
				<td class="">开户银行</td>
				<td>
					<xsl:value-of select="bankbranchname" />
				</td>
				<td class="">银行账号</td>
				<td>
					<xsl:value-of select="bankaccount" />
				</td>
			</tr>
			<tr>
				<td class="">本期对账金额</td>
				<td>
					<xsl:value-of select="format-number(number(payableamt),'#,##0.00')" />
				</td>
				<td class="">计划付款日期</td>
				<td>
					<xsl:value-of select="planpaydate" />
				</td>
			</tr>
			<tr>
				<td class="">17%税建议开票金额</td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt17),'#,##0.00')" />
				</td>
				<td class="">13%税建议开票金额 </td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt13),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="">0%税建议开票金额</td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt0),'#,##0.00')" />
				</td>
				<td class="">预付款冲抵金额</td>
				<td>
					<xsl:value-of select="format-number(number(suspayamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="">扣项一合计金额</td>
				<td>
					<xsl:if test="/xdoc/xout/sheet/@showType = 'detail'">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/sheet/charge_tax/row/chargeamt),'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/@showType = 'group'">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/sheet/group_charge_tax/row/chargeamt),'#,##0.00')" />
					</xsl:if>
				</td>
				<td class="">扣项二合计金额</td>
				<td>
					<xsl:if test="/xdoc/xout/sheet/@showType = 'detail'">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/sheet/charge_notax/row/chargeamt),'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/@showType = 'group'">
						<xsl:value-of
							select="format-number(sum(/xdoc/xout/sheet/group_charge_notax/row/chargeamt),'#,##0.00')" />
					</xsl:if>
				</td>
			</tr>
			<tr>
				<td class="">本期实付金额（大写）</td>
				<td>
					<xsl:value-of select="payamtToChinese" />
				</td>
				<td class="">本期实付金额（小写） </td>
				<td>
					<xsl:value-of select="format-number(number(payamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="">供应商盖章 </td>
				<td colspan="3">（盖章处） </td>
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
		<table class="center">
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
				<td colspan="4" class="tdHead">合计：</td>
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
		<table class="center">
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
				<td colspan="4" class="tdHead">合计：</td>
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
		<table class="center">
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
				<td colspan="4" class="tdHead">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/charge_notax/row/chargeamt),'#,##0.00')" />
				</td>
				<td colspan="1">
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_detail11">
		<table class="center">
			<tr>
				<th colspan="11" class="">对账单据</th>
			</tr>
			<tr>
				<th>单据类型</th>
				<th>门店编码</th>
				<th>门店名称</th>
				<th>对账金额</th>
				<th>单据发生日期</th>
				<th>结算方式</th>
				<th>课类信息</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
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
						<xsl:value-of select="docdate" />
					</td>
					<td>
						<xsl:value-of select="paytypename" />
					</td>
					<td>
						<xsl:value-of select="categoryid" />
						<xsl:value-of select="categoryname" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="3" class="tdHead">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/groupitem/row/totalpayamt),'#,##0.00')" />
				</td>
				<td colspan="3" class="tdHead">
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_detail21">
		<table class="center">
			<tr>
				<th colspan="6" class="tdHead">扣项一(票扣明细) </th>
			</tr>
			<tr>
				<th>扣项名称</th>
				<th>扣项金额</th>
				<th>扣项说明</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
					</td>
					<td>——</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="1" class="tdHead">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/group_charge_tax/row/chargeamt),'#,##0.00')" />
				</td>
				<td>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_detail31">
		<table class="center">
			<tr>
				<th colspan="6" class="tdHead">扣项二(非票扣明细) </th>
			</tr>
			<tr>
				<th>扣项名称</th>
				<th>扣项金额</th>
				<th>扣项说明</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
					</td>
					<td>
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="1" class="tdHead">合计：</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/group_charge_notax/row/chargeamt),'#,##0.00')" />
				</td>
				<td>
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>