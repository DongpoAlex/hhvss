<?xml version="1.0"  encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />

	<xsl:template match="/">
		<xsl:call-template name="output_module" />
	</xsl:template>

	<xsl:template name="output_module">
		<table width="80%" caption="模块清单">
			<tr>
				<th>模块编码</th>
				<th>模块名</th>
				<th>模块路径</th>
				<th>角色权限</th>
				<th>所属权限组</th>
			</tr>

			<xsl:for-each select="/xdoc/xout/rowset/row">
				<xsl:element name="tr">
					<xsl:if test="headroletype=1">
						<xsl:attribute name="class">deeper</xsl:attribute>
					</xsl:if>
					<xsl:if test="headroletype=2">
						<xsl:attribute name="class">lighter</xsl:attribute>
					</xsl:if>
					<xsl:if test="headroletype=0">
						<xsl:attribute name="class">pink</xsl:attribute>
					</xsl:if>
					<td>
						<xsl:value-of select="moduleid" />
					</td>
					<td>
						<xsl:value-of select="modulename" />
					</td>
					<td>
						<xsl:value-of select="action" />
					</td>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">
				   permission_manager_module.jsp?moduleid=<xsl:value-of
								select="moduleid" />
				</xsl:attribute>
							权限维护
						</xsl:element>

					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="headroletype=1">
							<xsl:attribute name="style">color:red;</xsl:attribute>
						</xsl:if>
						<xsl:if test="headroletype=2">
							<xsl:attribute name="style">color:green;</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="roletypename" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>


