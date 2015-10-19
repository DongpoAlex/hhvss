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
		<table width='100%' border='0'>
			<tr>
				<td width='170'>
					<xsl:element name="img">
						<xsl:attribute name="id">print_logo</xsl:attribute>
						<xsl:attribute name="style">margin-left:15px;</xsl:attribute>
						<xsl:attribute name="src"><xsl:value-of
							select="/xdoc/xout/sheet/@logo" /></xsl:attribute>
					</xsl:element>
				</td>
				<td align='center'>
					<div id='title' style='font-size: 18px;font-weight: bold;'>
						<xsl:value-of select="/xdoc/xout/sheet/@title" />
					</div>
				</td>
				<td width='241' align='right'>
					<xsl:element name="img">
						<xsl:attribute name="id">barcode</xsl:attribute>
						<xsl:attribute name="src">../BarCode?code=<xsl:value-of
							select="/xdoc/xout/sheet/head/row/sheetid" /></xsl:attribute>
					</xsl:element>
				</td>
			</tr>
		</table>
		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>
		<br />
		<xsl:for-each select="body/body1">
			<xsl:call-template name="output_detail1" />
		</xsl:for-each>
		<xsl:for-each select="body/body2">
			<xsl:call-template name="output_detail2" />
		</xsl:for-each>

	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td class="tdHead">单据编号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td class="tdHead">结算公司</td>
				<td>
					<xsl:value-of select="payshopid" />
					<xsl:value-of select="payshopname" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">商户编码</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td class="tdHead">商户名称</td>
				<td>
					<xsl:value-of select="vendername" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">费用单据数</td>
				<td>
					<xsl:value-of select="chargenum" />
				</td>
				<td class="tdHead">费用金额</td>
				<td>
					<xsl:value-of select="format-number(chargeamt,'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">滞纳金单据数</td>
				<td>
					<xsl:value-of select="latefeenum" />
				</td>
				<td class="tdHead">滞纳金金额</td>
				<td>
					<xsl:value-of select="format-number(latefeeamt,'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">业务员</td>
				<td>
					<xsl:value-of select="buyer" />
				</td>
				<td class="tdHead">合计金额</td>
				<td>
					<xsl:value-of select="format-number(latefeeamt+chargeamt,'#,##0.00')" />
				</td>
			</tr>
			<tr>
				<td class="tdHead">备注</td>
				<td colspan="3">
					<xsl:value-of select="note" />
				</td>
			</tr>
		</table>

	</xsl:template>

	<xsl:template name="output_detail1">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td colspan="12">费用明细</td>
			</tr>
			<tr>
				<th>机构编码</th>
				<th>机构名称</th>
				<th>费用单号</th>
				<th>费用名称</th>
				<th>费用发生日期</th>
				<th>费用应结日期</th>
				<th>费用金额</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="noteno" />
					</td>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td>
						<xsl:value-of select="docdate" />
					</td>
					<td>
						<xsl:value-of select="duedate" />
					</td>
					<td>
						<xsl:value-of select="format-number(chargeamt,'#,##0.00')" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="6">合计</td>
				<td>
					<xsl:value-of select="format-number(sum(row/chargeamt),'#,##0.00')" />
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template name="output_detail2">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td colspan="10">滞纳金明细</td>
			</tr>
			<tr>
				<th>机构编码</th>
				<th>机构名称</th>
				<th>费用单号</th>
				<th>费用名称</th>
				<th>费用应结日期</th>
				<th>费用结算日期</th>
				<th>滞纳金金额</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="noteno" />
					</td>
					<td>
						<xsl:value-of select="chargename" />
					</td>
					<td>
						<xsl:value-of select="duedate" />
					</td>
					<td>
						<xsl:value-of select="docdate" />
					</td>
					<td>
						<xsl:value-of select="format-number(latefeeamt,'#,##0.00')" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="6">合计</td>
				<td>
					<xsl:value-of select="format-number(sum(row/latefeeamt),'#,##0.00')" />
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>