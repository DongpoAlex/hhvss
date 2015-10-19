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
				功能模块清单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th"></xsl:element>
				<xsl:element name="th">
					菜单号
				</xsl:element>
				<xsl:element name="th">
					菜单名
				</xsl:element>
				<xsl:element name="th">
					角色类型
				</xsl:element>
				<xsl:element name="th">
					模块路径
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/rowset/row">
				<xsl:sort select="moduleid" order="ascending" data-type="number" />
				<xsl:element name="tr">
					<xsl:attribute name="class">deeper</xsl:attribute>
					<xsl:element name="td">
						<xsl:element name="input">
							<xsl:attribute name="type">radio</xsl:attribute>
							<xsl:attribute name="name">radio_module</xsl:attribute>
							<xsl:attribute name="moduleid"><xsl:value-of
								select="moduleid" /></xsl:attribute>
							<xsl:attribute name="menulabel"><xsl:value-of
								select="modulename" /></xsl:attribute>
							<xsl:attribute name="onclick">set_moduleid(this)</xsl:attribute>
						</xsl:element>

					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="moduleid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="modulename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="roletypename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="action" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


