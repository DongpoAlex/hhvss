<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:call-template name="output_sheet" />
	</xsl:template>

	<xsl:template name="output_sheet">
		<div id='div_print'>
			<a href='javascript:doPrint()'>[打印本页]</a>
		</div>
		<table width='100%' border='0'>
			<tr>
				<td width='170'>
					<xsl:element name="img">
						<xsl:attribute name="id">print_logo</xsl:attribute>
						<xsl:attribute name="style">margin-left:15px;</xsl:attribute>
						<xsl:attribute name="src">../img/<xsl:value-of
							select="/xdoc/xout/head/rows/booklogofname" /></xsl:attribute>
					</xsl:element>
				</td>
				<td align='center'>
					<div id='title' style='font-size: 18px;font-weight: bold;'>
						<xsl:value-of select="/xdoc/xout/head/rows/booktitle" />
						付款申请单
					</div>
				</td>
			</tr>
		</table>
		<xsl:call-template name="output_head" />
	</xsl:template>

	<xsl:template name="output_head">
		<xsl:element name="table">
			<xsl:attribute name="class">tablecolorborder</xsl:attribute>
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					城市公司
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bookname" />
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					支付方式
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/paymodename" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					单据编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/sheetid" />
				</xsl:element>

			</xsl:element>
			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					供应商编码
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/venderid" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					供应商名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/vendername" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					帐套名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bookname" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					计划付款日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/planpaydate" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					开户银行
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">3</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bankname" />
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					银行帐户
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">3</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/bankaccno" />
				</xsl:element>


			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					可建议支付金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="format-number(0,'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					应付账款余额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="format-number(0,'#,##0.00')" />
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					建议开票价税合计额17%
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(/xdoc/xout/head/rows/invtotalamt17,'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					建议开票价税合计额13%
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(/xdoc/xout/head/rows/invtotalamt13,'#,##0.00')" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本期对账金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/payamt)-number(/xdoc/xout/head/rows/chargeamt),'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本次费用合计
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/chargeamt)*-1,'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					建议开票价税合计额0%
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(/xdoc/xout/head/rows/invtotalamt0,'#,##0.00')" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					建议开票金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/invtotalamt17)+number(/xdoc/xout/head/rows/invtotalamt13)+number(/xdoc/xout/head/rows/invtotalamt0),'#,##0.00')" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					补税差
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">4</xsl:attribute>
					<xsl:if test="/xdoc/xout/head/rows/adjpayamt!=''">
						<xsl:value-of
							select="format-number(/xdoc/xout/head/rows/adjpayamt,'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/head/rows/adjpayamt=''">
						<xsl:value-of select="/xdoc/xout/head/rows/adjpayamt" />
					</xsl:if>
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					电汇费
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">2</xsl:attribute>
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:if test="/xdoc/xout/head/rows/financialfee!=''">
						<xsl:value-of
							select="format-number(/xdoc/xout/head/rows/financialfee,'#,##0.00')" />
					</xsl:if>
					<xsl:if test="/xdoc/xout/head/rows/financialfee=''">
						<xsl:value-of select="/xdoc/xout/head/rows/financialfee" />
					</xsl:if>
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					本期金额(大写)
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">4</xsl:attribute>
					(人民币)
					<xsl:value-of select="/xdoc/xout/head/rows/real_pay" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					实付金额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">2</xsl:attribute>
					<xsl:attribute name="class">decimal</xsl:attribute>
					￥
					<xsl:value-of
						select="format-number(number(/xdoc/xout/head/rows/payamt)-number(/xdoc/xout/head/rows/adjpayamt)-number(/xdoc/xout/head/rows/financialfee)-number(/xdoc/xout/head/rows/suspayamt),'#,##0.00')" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					备注
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">7</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/note" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					制单人
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/editor" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					制单日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">5</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/rows/editdate" />
				</xsl:element>
			</xsl:element>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>