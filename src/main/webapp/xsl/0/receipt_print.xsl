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
		<div id='div_print'>
			<a href='javascript:doPrint()'>[打印本页]</a>
		</div>

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
		<xsl:for-each select="body">
			<xsl:call-template name="output_body" />
		</xsl:for-each>
		<pre>
			注意：
			1、易腐烂、变味、变质的食品，请贵公司在我公司发出退货通知之日起三日内办理退货手续。
			2、一般商品，请贵公司在我公司发出退货通知之日起七日内办理退货手续。
			3、超过10天逾期未退商品，超市将做清货处理，货款将相应被扣除。
		</pre>
	</xsl:template>


	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>单据号</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td>
				</td>
				<td>
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
				<td>存货地</td>
				<td>
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
				<td>供货单位</td>
				<td>
					[
					<xsl:value-of select="venderid" />
					]
					<xsl:value-of select="vendername" />
				</td>
				<td>备注</td>
				<td>
					<xsl:value-of select="note" />
				</td>
			</tr>
		</table>
	</xsl:template>


	<xsl:template name="output_body">
		<table width="99%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>行号</td>
				<td>商品条码</td>
				<td>商品编码</td>
				<td>商品名称</td>
				<td>类别编码</td>
				<td>件数</td>
				<td>验收数量</td>
				<td>订货数量</td>
				<td>赠品数量</td>
				<td>进价</td>
				<td>销售规格</td>
				<td>进项税率</td>

				<td>进价小计</td>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="position()" />
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
						<xsl:value-of select="format-number(sumcost,'#,###.000') " />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="12">合计</td>
				<td>
					<xsl:value-of
						select="format-number(sum(/xdoc/xout/sheet/body/row/sumcost),'#,###.000')" />
				</td>
			</tr>
		</table>
	</xsl:template>


</xsl:stylesheet>