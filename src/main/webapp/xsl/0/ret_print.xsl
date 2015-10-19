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
	</xsl:template>

	<xsl:template name="output_head">
		<table width="99%" cellspacing="0" cellpadding="2" border="0">
			<tr>
				<td>单据编号：</td>
				<td>
					<xsl:value-of select="sheetid" />
				</td>
				<td>供应商：</td>
				<td>
					<xsl:value-of select="venderid" />
					【
					<xsl:value-of select="vendername" />
					】
				</td>
				<td>管理课：</td>
				<td>
					<xsl:value-of select="majorid" />
					【
					<xsl:value-of select="categoryname" />
					】
				</td>
			</tr>
			<tr>
				<td>退货门店：</td>
				<td>
					<xsl:value-of select="shopid" />
					【
					<xsl:value-of select="shopname" />
					】
				</td>
				<td>供应商地址：</td>
				<td>
					<xsl:value-of select="address" />
				</td>
				<td>联系电话：</td>
				<td>
					<xsl:value-of select="vendertel" />
				</td>
			</tr>
			<tr>
				<td>VSS接收时间：</td>
				<td>
					<xsl:value-of select="releasedate" />
				</td>
				<td colspan="2">备注：</td>
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
				<td>批次号</td>
				<td>商品编码</td>
				<td>条形码</td>
				<td>商品名称</td>
				<td>退货数</td>
				<td>实退数量</td>
				<td>实退金额</td>
				<td>退货原因</td>
			</tr>
			<xsl:for-each select="row">
				<tr>
					<td>
						<xsl:value-of select="position()" />
					</td>
					<td>
						<xsl:value-of select="goodscostid" />
					</td>
					<td>
						<xsl:value-of select="goodsid" />
					</td>
					<td>
						<xsl:value-of select="barcode" />
					</td>
					<td>
						<xsl:value-of select="goodsname" />
					</td>
					<td>
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="askqty" />
					</td>
					<td>
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="retqty" />
					</td>
					<td>
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(sumcost,'#0.00') " />
					</td>
					<td>
						<xsl:value-of select="reason" />
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<td colspan="5">合计:</td>
				<td>
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="sum(/xdoc/xout/sheet/body/row/askqty)" />
				</td>
				<td>
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="sum(/xdoc/xout/sheet/body/row/retqty)" />
				</td>
				<td>
					<xsl:attribute name="class">decimal</xsl:attribute>
					<xsl:value-of select="sum(/xdoc/xout/sheet/body/row/sumcost)" />
				</td>
				<td>
					<xsl:attribute name="colspan">1</xsl:attribute>
				</td>
			</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>