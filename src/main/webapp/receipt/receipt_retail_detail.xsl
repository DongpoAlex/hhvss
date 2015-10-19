<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- main start. -->
	<xsl:template match="/">
		<xsl:call-template name="show_detail" />
	</xsl:template>

	<xsl:template name="show_detail">
		<xsl:element name="div">
			<xsl:attribute name="class">box</xsl:attribute>
			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">title</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/head/@title" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						单据号：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/sheetid" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						供应商：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/venderid" />
					&#160;&#160;
					<xsl:value-of select="/xdoc/xout/sheet/head/row/vendername" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						收货地：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/houseid" />
					&#160;&#160;
					<xsl:value-of select="/xdoc/xout/sheet/head/row/housename" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						收货地址：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/address" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						收货地电话：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/telno" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						制单：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editdate" />
					&#160;&#160;
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editor" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						审核：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/checkdate" />
					&#160;&#160;
					<xsl:value-of select="/xdoc/xout/sheet/head/row/checker" />
				</xsl:element>



			</xsl:element>

			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						备注：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/note" />
				</xsl:element>
			</xsl:element>
		</xsl:element>

		<xsl:element name="table">
			<xsl:attribute name="class">tableborder</xsl:attribute>
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">100%</xsl:attribute>

			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="colspan">center</xsl:attribute>
					商品编码
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					商品名称
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					条形码
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					规格
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					重量
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					总数量
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					价钱
				</xsl:element>

				<xsl:element name="td">
					<xsl:attribute name="align">center</xsl:attribute>
					价钱税率
				</xsl:element>
			</xsl:element>


			<xsl:for-each select="/xdoc/xout/sheet/body/row">
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="goodsid" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="goodsname" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="barcode" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="unitname" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">decimal</xsl:attribute>
						<xsl:value-of select="spec" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="qty" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="cost" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">center</xsl:attribute>
						<xsl:value-of select="costtaxrate" />
					</xsl:element>

				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
