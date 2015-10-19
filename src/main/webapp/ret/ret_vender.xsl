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

		<xsl:element name="br" />
		<xsl:call-template name="output_body" />

		<xsl:element name="br" />
		业务员:
		<xsl:value-of select="head/row/editor" />
		主管:
		<xsl:value-of select="head/row/checker" />
	</xsl:template>


	<xsl:template name="output_head">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="th">
					单据号
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="sheetid" />
				</xsl:element>
				<xsl:element name="th">
					退货通知单号
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="refsheetid" />
				</xsl:element>

			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="th">
					供应商
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="venderid" />
					]
					<xsl:value-of select="vendername" />
				</xsl:element>

				<xsl:element name="th">
					门店
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="shopid" />
					]
					<xsl:value-of select="shopname" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="th">
					结算方式
				</xsl:element>
				<xsl:element name="td">
					[
					<xsl:value-of select="paytypeid" />
					]
					<xsl:value-of select="paytypename" />
				</xsl:element>

				<xsl:element name="th">
					退货日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:value-of select="retdate" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="tr">
					<xsl:element name="th">
						制单
					</xsl:element>
					<xsl:element name="td">
						(
						<xsl:value-of select="editor" />
						)
						<xsl:value-of select="editdate" />
					</xsl:element>

					<xsl:element name="th">
						审核
					</xsl:element>
					<xsl:element name="td">
						(
						<xsl:value-of select="checker" />
						)
						<xsl:value-of select="checkdate" />
					</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>


	<xsl:template name="output_body">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					商品编码
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					商品条码
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					商品名称
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					商品规格
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					商品单位
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					实退数量
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					进价
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					金额
				</xsl:element>

			</xsl:element>

			<xsl:for-each select="body/row">
				<xsl:element name="tr">

					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="goodsid" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="barcode" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="goodsname" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="spec" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="unitname" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="format-number(number(vldqty),'#,##0')" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(number(cost),'#,##0.00')" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="format-number(number(vldqty*cost),'#0.00')" />
					</xsl:element>

				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>