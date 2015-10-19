<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- main start. -->
	<xsl:template match="/">
		<xsl:call-template name="show_sheet" />
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr class="header">
				<td width="10%">备注</td>
				<td colspan="3" width="85%">
					1、我公司承诺以下所提交的发票，开具内容符合财政部颁布的《发票管理办法》
					及相关法律法规的规定，并与对应的付款申请单号包含的验收单商品品名内容保持一致，以此承担合同约定的法律风险及税务风险。
					<br />
					2、发票录入明细单一式两份，必须加盖公章或财务章。款项到账后请在VSS系统申请开具费用发票及凭此单领取。
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="show_sheet">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>发票录入单号</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/sheetid" />
				</td>
				<th>结算单号</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/pnsheetid" />
				</td>
			</tr>
			<tr>
				<th>供应商编码</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/venderid" />
				</td>
				<th>供应商名称</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
				</td>
			</tr>
			<tr>
				<th>生成日期</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editdate" />
				</td>
				<th>提交日期</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/checkdate" />
				</td>
			</tr>
			<tr>
				<th>联系人</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/contact" />
				</td>
				<th>联系电话</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/contacttel" />
				</td>
			</tr>
			<tr>
				<th>单据状态</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/flagname" />
				</td>
				<th>备注</th>
				<td>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/note" />
				</td>
			</tr>
		</table>

		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th colspan="9" class="tdHead">提交发票清单 </th>
			</tr>
			<tr>
				<th>发票类型</th>
				<th>发票号码</th>
				<th>发票代码</th>
				<th>发票日期</th>
				<th>税率</th>
				<th>税额</th>
				<th>价额</th>
				<th>价税合计</th>
				<th>说明</th>
			</tr>
			<xsl:for-each select="/xdoc/xout/sheet/body/row">
				<tr>
					<td>
						<xsl:value-of select="invoicetypename" />
					</td>
					<td>
						<xsl:value-of select="invoiceno" />
					</td>
					<td>
						<xsl:value-of select="invoicecode" />
					</td>
					<td>
						<xsl:value-of select="invoicedate" />
					</td>
					<td>
						<xsl:value-of select="taxrate" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(taxamt,'#,##0.00')" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(taxableamt,'#,##0.00')" />
					</td>
					<td class="decimal">
						<xsl:value-of select="format-number(taxableamt+taxamt,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="goodsdesc" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>合计</td>
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td>
				</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/taxamt),'#,##0.00')" />
				</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/taxableamt),'#,##0.00')" />
				</td>
				<td class="decimal">
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/taxableamt)+sum(/xdoc/xout/sheet/body/row/taxamt),'#,##0.00')" />
				</td>
				<td>
				</td>
			</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>
