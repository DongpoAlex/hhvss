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
				<xsl:call-template name="output_role" />
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


	<xsl:template name="output_role">
		<xsl:element name="table">
			<xsl:attribute name="width">70%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">1</xsl:attribute>
			<xsl:attribute name="rules">cols</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					是否成员
				</xsl:element>
				<xsl:element name="th">
					角色编号
				</xsl:element>
				<xsl:element name="th">
					角色名称
				</xsl:element>
				<xsl:element name="th">
					机构
				</xsl:element>
				<xsl:element name="th">
					描述
				</xsl:element>
				<xsl:element name="th">
					角色类型
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/role_list/role">
				<xsl:element name="tr">
					<xsl:if test="(position() mod 2)=1">
						<xsl:attribute name="class">deeper</xsl:attribute>
					</xsl:if>
					<xsl:if test="(position() mod 2)=0">
						<xsl:attribute name="class">lighter</xsl:attribute>
					</xsl:if>

					<xsl:element name="td">
						<xsl:element name="input">
							<xsl:attribute name="type">checkbox</xsl:attribute>
							<xsl:attribute name="name">box</xsl:attribute>
							<xsl:attribute name="roleid"><xsl:value-of
								select="@roleid" /></xsl:attribute>
							<xsl:attribute name="userid"><xsl:value-of
								select="@userid" /></xsl:attribute>
							<xsl:attribute name="roletype"><xsl:value-of
								select="@roletype" /></xsl:attribute>
							<xsl:attribute name="onclick">member_mgr(this)</xsl:attribute>
							<xsl:if test="@is_member">
								<xsl:attribute name="checked" />
							</xsl:if>
						</xsl:element>

					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@roleid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@rolename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@shopid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@note" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@roletypename" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


