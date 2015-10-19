<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" version="1.0">

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
				用户列表清单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					用户UserID
				</xsl:element>
				<xsl:element name="th">
					用户名
				</xsl:element>
				<xsl:element name="th">
					用户状态
				</xsl:element>
				<xsl:element name="th">
					登陆用户
				</xsl:element>
				<xsl:element name="th">
					机构编码
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/user_list/User">
				<xsl:sort select="@UserID" order="ascending" data-type="number" />
				<xsl:element name="tr">
					<xsl:element name="td">
						<xsl:value-of select="@UserID" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@UserName" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@UserStatus" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@LoginID" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@ShopID" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>
</xsl:stylesheet>