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
		<xsl:for-each select="head/row">
			<xsl:call-template name="output_head" />
		</xsl:for-each>

		<xsl:call-template name="output_body" />

	</xsl:template>


	<xsl:template name="output_head">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>
			<xsl:attribute name="rules">rows</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="th">
					单据号：
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="sheetid" />
				</xsl:element>

				<xsl:element name="th">
					退货地：
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="shopid" />
					]
					<xsl:value-of select="shopname" />
				</xsl:element>
				<xsl:element name="th">
					课类：
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="majorid" />
					]
					<xsl:value-of select="majorname" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					退货类型：
				</xsl:element>
				<xsl:element name="td">
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
				</xsl:element>

				<xsl:element name="th">
					申请地：
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="askshopid" />
					]
					<xsl:value-of select="askshopname" />
				</xsl:element>
				<xsl:element name="th"></xsl:element>
				<xsl:element name="td"></xsl:element>
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					柜组：
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="placeid" />
					]
					<xsl:value-of select="placename" />
				</xsl:element>

				<xsl:element name="th">
					结算方式：
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="paytypeid" />
					]
					<xsl:value-of select="paytypename" />
				</xsl:element>

				<xsl:element name="th">
					电话：
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="telno" />
				</xsl:element>
			</xsl:element>


			<xsl:element name="tr">
				<xsl:element name="th">
					供应商编号：
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="venderid" />
				</xsl:element>
				<xsl:element name="th">
					供应商名称：
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="vendername" />
				</xsl:element>

				<xsl:element name="th">
					传真：
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="faxno" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="th">
					备注：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="colspan">5</xsl:attribute>
					<xsl:value-of select="note" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>


	<xsl:template name="output_body">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>
			<xsl:attribute name="rules">rows</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					编码
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					条形码
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					商品名称
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					单位
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					规格
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					退货数
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					退货原因
				</xsl:element>
			</xsl:element>

			<xsl:for-each select="body/row">
				<xsl:element name="tr">
					<td style="text-align:center;">
						<xsl:value-of select="goodsid" />
					</td>

					<td style="text-align:center;">
						<xsl:value-of select="barcode" />
					</td>

					<td style="text-align:center;">
						<xsl:value-of select="goodsname" />
					</td>

					<td style="text-align:center;">
						<xsl:value-of select="unitname" />
					</td>

					<td style="text-align:center;">
						<xsl:value-of select="spec" />
					</td>

					<td style="text-align:center;">
						<xsl:value-of select="vldqty" />
					</td>

					<td style="text-align:center;">
						<xsl:value-of select="reason" />
					</td>
				</xsl:element>
			</xsl:for-each>
			<xsl:element name="tr">
				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					金额合计(元)：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					<xsl:value-of
						select="format-number(number( sum(body/row/costvalue) ),'#,##0.00')" />
				</xsl:element>

				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					业务员：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					<xsl:value-of select="head/row/operator" />
				</xsl:element>
				<xsl:element name="th">
					<xsl:attribute name="align">center</xsl:attribute>
					审核：
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					<xsl:value-of select="head/row/checker" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>


</xsl:stylesheet>