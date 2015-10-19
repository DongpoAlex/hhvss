<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<!-- main start. -->
	<xsl:template match="/">
		<xsl:call-template name="show_head" />
		<xsl:call-template name="show_sheetlist" />
	</xsl:template>
	<xsl:template name="show_head">
		<xsl:element name="table">
			<xsl:attribute name="class">tableborder</xsl:attribute>
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					单据编号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/sheetid" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					城市公司
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/bookname" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					制单人
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editor" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					制单日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/editdate" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					审核人
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/checker " />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					审核日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/checkdate " />
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					最近修改人
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/toucher" />
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">header</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					最近修改日期
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:value-of select="/xdoc/xout/sheet/head/row/touchtime" />
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="show_sheetlist">
		<xsl:element name="table">
			<xsl:attribute name="class">tableborder</xsl:attribute>
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="cellpadding">2</xsl:attribute>
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					<xsl:attribute name="colspan">10</xsl:attribute>
					对帐单据
				</xsl:element>
			</xsl:element>

			<xsl:element name="tr">
				<xsl:attribute name="class">header</xsl:attribute>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					单据号
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">left</xsl:attribute>
					单据类型
				</xsl:element>
				<xsl:element name="td">
					<xsl:attribute name="align">right</xsl:attribute>
					单据金额
				</xsl:element>
				<xsl:for-each select="/xdoc/xout/sheet/body/row">
					<xsl:element name="tr">
						<xsl:attribute name="class">
							altbg2
						</xsl:attribute>
						<xsl:element name="td">
							<xsl:attribute name="align">left</xsl:attribute>
							<xsl:value-of select="noteno" />
						</xsl:element>
						<xsl:element name="td">
							<xsl:attribute name="align">left</xsl:attribute>
							<xsl:value-of select="name" />
						</xsl:element>
						<xsl:element name="td">
							<xsl:attribute name="align">right</xsl:attribute>
							<xsl:value-of select="notevalue" />
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
				<xsl:element name="tr">
					<xsl:attribute name="class">altbg2</xsl:attribute>
					<xsl:element name="td">
						<xsl:attribute name="class">
							header
						</xsl:attribute>
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:attribute name="colspan">3</xsl:attribute>
						合计:
						<xsl:if test="/xdoc/xout/sheet/body/row/notevalue!=''">
							<xsl:value-of
								select="format-number(sum(/xdoc/xout/sheet/body/row/notevalue),'#,##0.00')" />
						</xsl:if>
					</xsl:element>

				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
