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
		<xsl:for-each select="body">
			<xsl:call-template name="output_body" />
		</xsl:for-each>
	</xsl:template>


	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>单据号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td>审核人</td>
				<td>
					<xsl:value-of select="checker" />
				</td>
			</tr>
			<tr>
				<td>订货单号</td>
				<td>
					<xsl:value-of select="refsheetid" />
				</td>
				<td>VSS接收时间</td>
				<td>
					<xsl:value-of select="releasedate" />
				</td>
			</tr>
			<tr>
				<td>填单日期</td>
				<td>
					<xsl:value-of select="editdate" />
				</td>
				<td>审核日期</td>
				<td>
					<xsl:value-of select="checkdate" />
				</td>
			</tr>
			<tr>
				<td>结算方式</td>
				<td>
					[
					<xsl:value-of select="paytypeid" />
					]
					<xsl:value-of select="paytypename" />
				</td>
				<td>送货方式</td>
				<td>
					<xsl:value-of select="logisticsname" />
				</td>
			</tr>
			<tr>
				<td>供货单位</td>
				<td>
					[
					<xsl:value-of select="venderid" />
					]
					<xsl:value-of select="vendername" />
				</td>
				<td>收货单位</td>
				<td>
					[
					<xsl:value-of select="shopid" />
					]
					<xsl:value-of select="shopname" />
				</td>
			</tr>
			<tr>

				<td>备注</td>
				<td colspan="3">
					<xsl:value-of select="note" />
				</td>
			</tr>
		</table>
	</xsl:template>


	<xsl:template name="output_body">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<th>行号</th>
				<th>门店编码</th>
				<th>门店名称</th>
				<th>商品条码</th>
				<th>商品编码</th>
				<th>商品名称</th>
				<th>类别编码</th>
				<th>件数</th>
				<th>验收数量</th>
				<th>订货数量</th>
				<th>赠品数量</th>
				<th>进价</th>
				<th>销售规格</th>
				<th>进项税率</th>
				<th>存货地</th>
				<td>进价小计</td>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="position()" />
					</td>
					<td>
						<xsl:value-of select="shopid" />
					</td>
					<td>
						<xsl:value-of select="shopname" />
					</td>
					<td>
						<xsl:value-of select="barcode" />
					</td>
					<td>
						<xsl:value-of select="goodsid" />
					</td>
					<td>
						<xsl:value-of select="goodsname" />
					</td>
					<td>
						<xsl:value-of select="deptid" />
					</td>
					<td>
						<xsl:value-of select="pknum" />
					</td>
					<td>
						<xsl:value-of select="qty" />
					</td>
					<td>
						<xsl:value-of select="orderqty" />
					</td>
					<td>
						<xsl:value-of select="giftqty" />
					</td>
					<td>
						<xsl:value-of select="cost" />
					</td>
					<td>
						<xsl:value-of select="spec" />
					</td>
					<td>
						<xsl:value-of select="costtaxrate" />
					</td>
					<td>
						[
						<xsl:value-of select="placeid" />
						]
						<xsl:value-of select="placename" />
					</td>
					<td>
						<xsl:value-of select="format-number(sumcost,'#,###.000') " />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td>合计</td>
				<td colspan="14">
				</td>
				<td>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/sumcost),'#,###.000')" />
				</td>
			</tr>
		</table>
	</xsl:template>


</xsl:stylesheet>