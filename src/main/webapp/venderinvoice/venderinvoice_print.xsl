<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- main start. -->
	<xsl:template match="/">
		<xsl:call-template name="show_head" />
		<xsl:call-template name="show_sheetlist" />
		<xsl:call-template name="show_foot" />
	</xsl:template>

	<xsl:template name="show_head">
		<xsl:element name="table">
			<xsl:attribute name="class">tableborder</xsl:attribute>
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					发票提交单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/sheetid" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					付款申请单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/refsheetid" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					供应商编码
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/venderid" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					供应商名称
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					填写日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editdate" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					提交日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/checkdate" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					提交联系人
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/contact" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					联系电话
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/contacttel" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="show_sheetlist">
		<xsl:element name="table">
			<xsl:attribute name="class">tableborder</xsl:attribute>
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">100%</xsl:attribute>

			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="colspan">8</xsl:attribute>
					发票详细信息列表
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					发票号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					发票类型号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					发票日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					税额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					税率
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					价额
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					价税合计
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					发票说明
				</xsl:element>
			</xsl:element>

			<xsl:for-each select="/xdoc/xout/sheet/body/row">

				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="invoiceno" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="invoicetype" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="invoicedate" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(taxamt,'#,##0.00')" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="taxrate" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(taxableamt,'#,##0.00')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(taxableamt+taxamt,'#,##0.00')" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="goodsdesc" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>

			<xsl:element name="tr">
				<xsl:attribute name="class">altbg2</xsl:attribute>
				<td align="center">合计</td>
				<td></td>
				<td></td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/taxamt),'#,##0.00')" />
				</td>
				<td></td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/taxableamt),'#,##0.00')" />
				</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/taxableamt)+sum(/xdoc/xout/sheet/body/row/taxamt),'#,##0.00')" />
				</td>
				<td></td>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="show_foot">
		<table class="tableborder" align="center" width="100%">
			<tr class="header">
				<td width="10%">备注</td>
				<td colspan="3" width="85%">
					1、我公司承诺以下所提交的发票，开具内容符合财政部颁布的《发票管理办法》
					及相关法律法规的规定，并与对应的付款申请单号包含的验收单商品品名内容保持一致，以此承担合同约定的法律风险及税务风险。
					<br />
					2、发票录入明细单一式两份，必须加盖公章或财务章，款项到帐后请凭此单开具费用发票。
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>
