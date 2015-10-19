<?xml version="1.0"  encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code = 100">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					<xsl:value-of select="xdoc/xerr/note" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:call-template name="output_list" />
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


	<xsl:template name="output_list">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">cols</xsl:attribute>
			<xsl:element name="caption">
				可选菜单清单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th"></xsl:element>
				<xsl:element name="th">
					菜单号
				</xsl:element>
				<xsl:element name="th">
					菜单名
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/menu_list/menu">
				<xsl:element name="tr">
					<xsl:attribute name="class">deeper</xsl:attribute>
					<xsl:element name="td">
						<xsl:element name="input">
							<xsl:attribute name="type">radio</xsl:attribute>
							<xsl:attribute name="name">radio_menuroot</xsl:attribute>
							<xsl:attribute name="menuid"><xsl:value-of
								select="@menuid" /></xsl:attribute>
							<xsl:attribute name="onclick">set_menuroot(this)</xsl:attribute>
						</xsl:element>

					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@menuid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@menulabel" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


