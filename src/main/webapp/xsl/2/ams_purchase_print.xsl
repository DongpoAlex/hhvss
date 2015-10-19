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
		<xsl:for-each select="body">
			<xsl:call-template name="output_detail" />
		</xsl:for-each>
		<br />
		经办人:
		<xsl:value-of select="head/row/editor" />
		部门负责人:
		<xsl:value-of select="head/row/checker" />
		<hr />
	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="4" border="1">
			<tr>
				<td>单据编号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td>合同单号</td>
				<td>
					<xsl:value-of select="refsheetid" />
				</td>
				<td>物理合同号</td>
				<td>-</td>
			</tr>
			<tr>
				<td>入账公司</td>
				<td>
					<xsl:value-of select="shopid" />
					<xsl:value-of select="shopname" />
				</td>
				<td>收货部门</td>
				<td colspan="3">
					<xsl:value-of select="departmentid" />
					<xsl:value-of select="departmentname" />
				</td>
			</tr>
			<tr>
				<td>供应商</td>
				<td>
					<xsl:value-of select="venderid" />
					<xsl:value-of select="vendername" />
				</td>
				<td>联系人</td>
				<td>
					<xsl:value-of select="venderlinkman" />
				</td>
				<td>联系电话</td>
				<td>
					<xsl:value-of select="venderlinkmode" />
				</td>

			</tr>
			<tr>
				<td>联系地址</td>
				<td>
					<xsl:value-of select="venderaddress" />
				</td>
				<td>联系传真</td>
				<td colspan="3">
					<xsl:value-of select="faxno" />
				</td>
			</tr>
			<tr>
				<td>收货地址</td>
				<td>
					<xsl:value-of select="receaddress" />
				</td>
				<td>联系人</td>
				<td>
					<xsl:value-of select="recelinkman" />
				</td>
				<td>联系电话</td>
				<td>
					<xsl:value-of select="recelinkmode" />
				</td>
			</tr>
			<tr>
				<td>纳税人识别号</td>
				<td>
					<xsl:value-of select="taxpayerid" />
				</td>
				<td>开票名称</td>
				<td colspan="3">
					<xsl:value-of select="taxpayername" />
				</td>
			</tr>
			<tr>
				<td>地址</td>
				<td>
					<xsl:value-of select="address" />
				</td>
				<td>电话</td>
				<td colspan="3">
					<xsl:value-of select="telno" />
				</td>
			</tr>
			<tr>
				<td>开户银行</td>
				<td>
					<xsl:value-of select="bank" />
				</td>
				<td>开户帐号</td>
				<td colspan="3">
					<xsl:value-of select="accountno" />
				</td>
			</tr>
			<tr>
				<td>操作员</td>
				<td>
					<xsl:value-of select="operator" />
				</td>
				<td>截至送货日期</td>
				<td>
					<xsl:value-of select="deliverydate" />
				</td>
				<td>安装费</td>
				<td>
					<xsl:value-of select="settingvalue" />
				</td>
			</tr>
			<tr>
				<td>收发票地址</td>
				<td>
					<xsl:value-of select="invreclinkadd" />
				</td>
				<td>收发票联系人</td>
				<td>
					<xsl:value-of select="invreclinkman" />
				</td>
				<td>收发票联系方式</td>
				<td>
					<xsl:value-of select="invreclinkmode" />
				</td>
			</tr>
			<tr>
				<td>配件费</td>
				<td>
					<xsl:value-of select="fittingvalue" />
				</td>
				<td>备注</td>
				<td colspan="3">
					<xsl:value-of select="note" />
				</td>
			</tr>
		</table>

	</xsl:template>

	<xsl:template name="output_detail">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>资产编码</th>
				<th>资产名称</th>
				<th>资产规格</th>
				<th>订货数量</th>
				<th>协议进价</th>
				<th>协议订货金额</th>
				<th>订货金额</th>
				<th>税率</th>
				<th>资产设备清单</th>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="assetcode" />
					</td>
					<td>
						<xsl:value-of select="assetname" />
					</td>
					<td>
						<xsl:value-of select="assetspec" />
					</td>
					<td>
						<xsl:value-of select="qty" />
					</td>
					<td>
						<xsl:value-of select="format-number(confercost,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(xydh,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(dh,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="taxrate" />
						%
					</td>
					<td>
						<xsl:value-of select="assetfixture" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="3">合计</td>
				<td>
					<xsl:value-of select="sum(row/qty)" />
				</td>
				<td>
				</td>
				<td>
					<xsl:value-of select="sum(row/xydh)" />
				</td>
				<td>
					<xsl:value-of select="sum(row/dh)" />
				</td>
				<td>
				</td>
				<td>
				</td>
			</tr>
		</table>
	</xsl:template>
</xsl:stylesheet>