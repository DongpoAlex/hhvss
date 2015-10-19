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
			<xsl:element name="br" />
			<xsl:element name="br" />
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
				<td>退货地：</td>
				<td>
					[
					<xsl:value-of select="shopid" />
					]
					<xsl:value-of select="shopname" />
				</td>
			</tr>
			<tr>
				<td>执行退货时间：</td>
				<td>
					<xsl:value-of select="retdate" />
				</td>
				<td>退货类型：</td>
				<td>
					<xsl:if test="rettype=0">
						普通退货
					</xsl:if>
					<xsl:if test="rettype=1">
						特殊退货
					</xsl:if>
					<xsl:if test="rettype=2">
						清场退货
					</xsl:if>
					<xsl:if test="rettype=3">
						清品退货
					</xsl:if>
				</td>
			</tr>
			<tr>
				<td>课类：</td>
				<td>
					[
					<xsl:value-of select="majorid" />
					]
					<xsl:value-of select="categoryname" />
				</td>
				<td>柜组：</td>
				<td>
					[
					<xsl:value-of select="placeid" />
					]
					<xsl:value-of select="placename" />
				</td>
			</tr>
			<tr>
				<td>申请地：</td>
				<td>
					[
					<xsl:value-of select="shopid" />
					]
					<xsl:value-of select="shopname" />
				</td>
				<td>电话：</td>
				<td>
					<xsl:value-of select="vendertel" />
				</td>
			</tr>
			<tr>
				<td>结算方式：</td>
				<td>
					[
					<xsl:value-of select="paytypeid" />
					]
					<xsl:value-of select="paytypename" />
				</td>
				<td>传真：</td>
				<td>
					<xsl:value-of select="venderfax" />
				</td>
			</tr>
			<tr>
				<td>供应商编号：</td>
				<td>
					<xsl:value-of select="venderid" />
				</td>
				<td>VSS接收时间：</td>
				<td>
					<xsl:value-of select="releasedate" />
				</td>

			</tr>
			<tr>
				<td>供应商名称：</td>
				<td colspan="3">
					<xsl:value-of select="vendername" />
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
				<td>行号</td>
				<td>批次号</td>
				<td>商品编码</td>
				<td>商品条码</td>
				<td>商品名称</td>
				<td>单位</td>
				<td>规格</td>
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
						<xsl:value-of select="unitname" />
					</td>
					<td>
						<xsl:value-of select="spec" />
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
				<td colspan="7">合计：</td>
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