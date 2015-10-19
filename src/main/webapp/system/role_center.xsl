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
					系统中目前没有发现您要的数据。
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
			<xsl:attribute name="width">90%</xsl:attribute>
			<xsl:attribute name="Align">center</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">cols</xsl:attribute>
			<xsl:element name="caption">
				角色清单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					机构
				</xsl:element>
				<xsl:element name="th">
					角色ID
				</xsl:element>
				<xsl:element name="th">
					角色名称
				</xsl:element>
				<xsl:element name="th">
					描述
				</xsl:element>
				<xsl:element name="th">
					所属权限
				</xsl:element>
				<xsl:element name="th">
					模块权限
				</xsl:element>
				<xsl:element name="th">
					删除
				</xsl:element>

			</xsl:element>
			<xsl:for-each select="/xdoc/xout/role_list/role">
				<xsl:sort select="shopid" order="ascending" data-type="text" />
				<xsl:sort select="roleid" order="ascending" data-type="number" />
				<xsl:element name="tr">
					<xsl:if test="(position() mod 2)=1">
						<xsl:attribute name="class">deeper</xsl:attribute>
					</xsl:if>
					<xsl:if test="(position() mod 2)=0">
						<xsl:attribute name="class">lighter</xsl:attribute>
					</xsl:if>
					<xsl:element name="td">
						<xsl:value-of select="shopid" />
						&#160;
						<xsl:value-of select="shopname" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="width">50</xsl:attribute>
						<xsl:value-of select="roleid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">
					role_manager.jsp?roleid=<xsl:value-of select="roleid" />
					</xsl:attribute>
							<xsl:value-of select="rolename" />
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="note" />
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
					<xsl:element name="td">
						<xsl:attribute name="width">50</xsl:attribute>
						<xsl:element name="a">
							<xsl:attribute name="href">
						permission_manager_role.jsp?roleid=<xsl:value-of
								select="roleid" />
					</xsl:attribute>
							权限维护
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:attribute name="width">50</xsl:attribute>
						<xsl:element name="a">
							<xsl:attribute name="href">
						javascript:delete_role('<xsl:value-of select="roleid" />')
					</xsl:attribute>
							删除
						</xsl:element>
					</xsl:element>

				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


