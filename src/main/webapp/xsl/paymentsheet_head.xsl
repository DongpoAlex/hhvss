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
		<style>
			BODY{
			BACKGROUND-COLOR: #fff;
			}
			table{background-color:#000;width: 100%;padding:
			0px;border:#000 1px solid;}
			td,th{background-color:#ffffff;font-size:
			12px;}
			.center td,th{text-align: center;}
			#div_printhelp{
			width: 400px;border: 1px green dotted;padding:
			16px;color:white;display:none;
			position: absolute;
			top: 0;
			left: 300;
			z-index: 100;
			background-color: blue;
			filter:progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=90,finishOpacity=90);
			}
		</style>

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
	</xsl:template>
	<xsl:template name="output_head">
		<table>
			<tr>

				<td class="" width="12%">BU</td>
				<td>
					<xsl:value-of select="buname" />
				</td>
				<td class="" width="12%">支付方式</td>
				<td>
					<xsl:value-of select="paymodename" />
					[
					<xsl:value-of select="daypay" />
					]
				</td>
				<td class="" width="12%">结算申请单号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
			</tr>
			<tr>
				<td class="">结算主体</td>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/taxername" />
				</td>
				<td class="">供应商编码</td>
				<td>
					<xsl:value-of select="venderid" />
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
				<td class="">计划付款日</td>
				<td>
					<xsl:value-of select="planpaydate" />
				</td>
			</tr>
			<tr>
				<td class="">
				</td>
				<td>
				</td>
				<td class="">建议开票价税合计额17%</td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt17),'#,##0.00')" />
				</td>
				<td class="">建议开票价税合计额13% </td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt13),'#,##0.00')" />
				</td>

			</tr>
			<tr>
				<td class="">本期对账金额</td>
				<td>
					<xsl:value-of select="format-number(number(payableamt),'#,##0.00')" />
				</td>
				<td class="">建议开票价税合计额0%</td>
				<td>
					<xsl:value-of select="format-number(number(invtotalamt0),'#,##0.00')" />
				</td>
				<td class="">建议开票金额合计</td>
				<td>
					<xsl:value-of
						select="format-number(number(invtotalamt0+invtotalamt13+invtotalamt17),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="">本次费用合计</td>
				<td>
					<xsl:value-of select="format-number(number(chargeamt),'#,##0.00')" />
				</td>
				<td class="">收益</td>
				<td>0.00</td>
				<td class="">预付款冲抵金额</td>
				<td>
					<xsl:value-of select="format-number(number(suspayamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="" colspan="6">财务费用</td>
			</tr>
			<tr>
				<td class="">电汇费</td>
				<td>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/sheet/finfee/row/amt1),'#,##0.00')" />
				</td>
				<td class="">扣税差</td>
				<td colspan="3">
					<xsl:value-of
						select="format-number(number(/xdoc/xout/sheet/finfee/row/amt2),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="">进项税转出</td>
				<td>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/sheet/finfee/row/amt5),'#,##0.00')" />
				</td>
				<td class="">提前付款折扣</td>
				<td colspan="3">
					<xsl:value-of
						select="format-number(number(/xdoc/xout/sheet/finfee/row/amt3),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="">财务费用合计</td>
				<td colspan="5">
					<xsl:value-of
						select="format-number(number(/xdoc/xout/sheet/finfee/row/amtother),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td colspan="4">
					本期金额（大写）-人民币
					<xsl:value-of select="payamtToChinese" />
				</td>
				<td class="">实付金额 </td>
				<td>
					<xsl:value-of select="format-number(number(payamt),'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="">备注</td>
				<td colspan="5">
					<xsl:value-of select="note" />
				</td>
			</tr>
			<tr>
				<td class="">制单人</td>
				<td>
					<xsl:value-of select="editor" />
				</td>
				<td class="">发票确认人</td>
				<td>
					<xsl:value-of select="inveditor" />
				</td>
				<td class="">审批人</td>
				<td>
					<xsl:value-of select="approver" />
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>