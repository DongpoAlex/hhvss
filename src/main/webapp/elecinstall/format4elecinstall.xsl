<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code = 100">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					系统中目前没有发现您要的单据。
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:for-each select="/xdoc/xout/sheet">
					<xsl:call-template name="output_sheet" />
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="div">
					<xsl:attribute name="class">warning</xsl:attribute>
					<xsl:value-of select="xdoc/xerr/code" />
					:
					<xsl:value-of select="xdoc/xerr/note" />
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="output_sheet">
		<div align='center' style='font-size: 18px;font-weight: bold;'>
			<xsl:value-of select="/xdoc/xout/sheet/@sheetname" />
		</div>
		<br />
		<xsl:call-template name="output_head" />
		<xsl:call-template name="output_detail" />
	</xsl:template>


	<xsl:template name="output_head">
		<table width="100%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>
					<xsl:if test="/xdoc/xout/sheet/head/rows/sheettype=0">
						安装单号：
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/sheettype=1">
						取消单号：
					</xsl:if>
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/sheetid" />
				</td>
				<td>
					<xsl:if test="/xdoc/xout/sheet/head/rows/sheettype=0">
						提货单号：
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/sheettype=1">
						安装单号：
					</xsl:if>
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/refsheetid" />
				</td>
				<td>
					销售门店：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/shopid" />
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/shopname" />
				</td>
				<td colspan="2">
					销售日期：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/saledate" />
				</td>
				<td>
					POS机号：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/posid" />
				</td>
			</tr>
			<tr>
				<td>
					顾客姓名：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/customer" />
				</td>
				<td>
					联系人：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/cman" />
				</td>
				<td>
					顾客手机：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/mphone" />
				</td>
				<td>
					联系电话：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/telephone" />
				</td>
				<td colspan="2">
					送货地址：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/address" />
				</td>
			</tr>
			<tr>

				<td>
					实际送货日期：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/tdeliverdate" />
				</td>
				<td>
					实际送货时间段：
					<xsl:if test="/xdoc/xout/sheet/head/rows/ttimeid=0">
						上午
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/ttimeid=1">
						下午
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/ttimeid=2">
						晚上
					</xsl:if>
				</td>
				<td>
					送货日期：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/deliverdate" />
				</td>
				<td colspan="3">
					送货时间段：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/btime" />
				</td>
			</tr>
			<tr>
				<td>
					单据状态：
					<xsl:if test="/xdoc/xout/sheet/head/rows/flag=0">
						编辑
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/flag=1">
						门店审核
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/flag=2">
						DC审核
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/flag=3">
						已出货(发货地执行)
					</xsl:if>
					<xsl:if test="/xdoc/xout/sheet/head/rows/flag=99">
						取消
					</xsl:if>
				</td>
				<td>
					制单人：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/editer" />
				</td>
				<td>
					制单日期：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/editdate" />
				</td>
				<td colspan="3">
					备注：
					<xsl:value-of select="/xdoc/xout/sheet/head/rows/notes" />
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="output_detail">
		<br />
		<table width="100%" cellspacing="0" cellpadding="2" border="1">
			<tr>
				<td>商品编码：</td>
				<td>商品条码</td>
				<td>商品名称</td>
				<td>柜组</td>
				<td>柜组名称</td>
				<td>售价</td>
				<td>数量</td>
				<td>金额</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/sheet/body/rows">
				<tr>
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
						<xsl:value-of select="placeid" />
					</td>
					<td>
						<xsl:value-of select="placename" />
					</td>
					<td class="decimal">
						<xsl:value-of select="price" />
					</td>
					<td>
						<xsl:value-of select="qty" />
					</td>
					<td>
						<xsl:value-of
							select="format-number((number(qty)*number(price)),'#,##0.00')" />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>