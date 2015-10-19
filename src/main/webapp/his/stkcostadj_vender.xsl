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
			<xsl:when test="xdoc/xerr/code=-1">
				<xsl:element name="div">
					<xsl:attribute name="class">warning</xsl:attribute>
					此单据没有单据明细.
				</xsl:element>
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
		<xsl:for-each select="head">
			<xsl:call-template name="output_head" />
		</xsl:for-each>


		<xsl:element name="br" />

		<xsl:for-each select="body[ @name='stkcostadjitem' ]">
			<xsl:call-template name="output_detail" />
		</xsl:for-each>

		<xsl:element name="br" />


	</xsl:template>


	<xsl:template name="output_head">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">1</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>

			<xsl:for-each select="row">
				<xsl:element name="tr">
					<xsl:element name="th">
						单号
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="sheetid" />
					</xsl:element>

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
						门店
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="th">
						供应商
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="vendername" />
					</xsl:element>
				</xsl:element>

				<xsl:element name="tr">
					<xsl:element name="th">
						未含税金额
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="totalamt" />
					</xsl:element>

					<xsl:element name="th">
						17税额
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="totaltaxamt17" />
					</xsl:element>


					<xsl:element name="th">
						13税额
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="totaltaxamt13" />
					</xsl:element>

					<xsl:element name="th">
						价税合计
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of
							select="format-number(totaltaxamt13+totaltaxamt17+totalamt,'#,###.00')" />
					</xsl:element>
				</xsl:element>

				<xsl:element name="tr">
					<xsl:element name="th">
						制单人
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="editor" />
					</xsl:element>

					<xsl:element name="th">
						制单日期
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="editdate" />
					</xsl:element>

					<xsl:element name="th">
						审核人
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="checker" />
					</xsl:element>

					<xsl:element name="th">
						审核日期
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="checkdate" />
					</xsl:element>
				</xsl:element>

				<xsl:element name="tr">
					<xsl:element name="th">
						备注
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="colspan">7</xsl:attribute>
						<xsl:value-of select="note" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>


	<xsl:template name="output_detail">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>
			<xsl:attribute name="border">1</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="th">
					门店
				</xsl:element>
				<xsl:element name="th">
					商品编码
				</xsl:element>
				<xsl:element name="th">
					条形码
				</xsl:element>
				<xsl:element name="th">
					商品名称
				</xsl:element>
				<xsl:element name="th">
					类别
				</xsl:element>
				<xsl:element name="th">
					数量
				</xsl:element>
				<xsl:element name="th">
					原成本单价
				</xsl:element>
				<xsl:element name="th">
					新成本单价
				</xsl:element>
				<xsl:element name="th">
					调整金额
				</xsl:element>
				<xsl:element name="th">
					进项税率
				</xsl:element>
				<xsl:element name="th">
					销项税率
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="row">
				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="goodsid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="barcode" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="goodsname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="categoryname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="qty" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="oldcost" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="newcost" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="adjamt" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="costtaxrate" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="saletaxrate" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
