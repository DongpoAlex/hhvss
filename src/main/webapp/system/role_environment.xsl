<?xml version="1.0"  encoding="UTF-8" ?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code = 100 ">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					<xsl:value-of select="xdoc/xerr/note" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:call-template name="output_environment" />
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


	<xsl:template name="output_environment">
		<xsl:element name="table">
			<xsl:attribute name="width">50%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">cols</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					变量名
				</xsl:element>
				<xsl:element name="th">
					变量值
				</xsl:element>
				<xsl:element name="th">
					操作
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/environment_set/environment">
				<xsl:element name="tr">
					<xsl:if test="(position() mod 2)=1">
						<xsl:attribute name="class">deeper</xsl:attribute>
					</xsl:if>
					<xsl:if test="(position() mod 2)=0">
						<xsl:attribute name="class">lighter</xsl:attribute>
					</xsl:if>
					<xsl:element name="td">
						<xsl:value-of select="@name" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@value" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="width">40</xsl:attribute>
						<xsl:element name="a">
							<xsl:attribute name="href">
						javascript:delete_env('<xsl:value-of select="@name" />', '<xsl:value-of
								select="@value" />')
					</xsl:attribute>
							删除
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


