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
			<xsl:attribute name="width">70%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">cols</xsl:attribute>
			<xsl:element name="caption">
				系统用户清单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					用户ID
				</xsl:element>
				<xsl:element name="th">
					登录名
				</xsl:element>
				<xsl:element name="th">
					用户名
				</xsl:element>
				<xsl:element name="th">
					机构号
				</xsl:element>
				<xsl:element name="th">
					机构名称
				</xsl:element>
				<xsl:element name="th">
					根菜单
				</xsl:element>
				<xsl:element name="th"></xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/user_list/user">
				<xsl:sort select="shopid" order="ascending" data-type="text" />
				<xsl:sort select="loginid" order="ascending" data-type="text" />
				<xsl:element name="tr">
					<xsl:if test="(position() mod 2)=1">
						<xsl:attribute name="class">deeper</xsl:attribute>
					</xsl:if>
					<xsl:if test="(position() mod 2)=0">
						<xsl:attribute name="class">lighter</xsl:attribute>
					</xsl:if>
					<xsl:element name="td">
						<xsl:value-of select="userid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="target">_blank</xsl:attribute>
							<xsl:attribute name="href">
				   user_manager.jsp?userid=<xsl:value-of select="userid" />
				</xsl:attribute>
							<xsl:value-of select="loginid" />
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="username" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="menulabel" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="target">_blank</xsl:attribute>
							<xsl:attribute name="href">
				   role4user.jsp?userid=<xsl:value-of select="userid" />
				</xsl:attribute>
							用户角色维护
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>
