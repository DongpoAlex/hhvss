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
				<xsl:call-template name="output_roleright" />
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


	<xsl:template name="output_roleright">
		<xsl:element name="table">
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">90%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">cols</xsl:attribute>
			<xsl:element name="caption">
				角色
				<xsl:value-of select="/xdoc/authority_list/authority/@roleid" />
				权限清单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					模块编码
				</xsl:element>
				<xsl:element name="th">
					模块名称
				</xsl:element>
				<xsl:element name="th">
					查看
				</xsl:element>
				<xsl:element name="th">
					修改
				</xsl:element>
				<xsl:element name="th">
					添加
				</xsl:element>
				<xsl:element name="th">
					删除
				</xsl:element>
				<xsl:element name="th">
					打印
				</xsl:element>
				<xsl:element name="th">
					审核一
				</xsl:element>
				<xsl:element name="th">
					审核二
				</xsl:element>
				<xsl:element name="th">
					审核三
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/authority_list/authority">
				<xsl:sort select="@moduleid" order="ascending" data-type="number" />
				<xsl:element name="tr">
					<xsl:if test="(position() mod 2)=1">
						<xsl:attribute name="class">deeper</xsl:attribute>
					</xsl:if>
					<xsl:if test="(position() mod 2)=0">
						<xsl:attribute name="class">lighter</xsl:attribute>
					</xsl:if>
					<xsl:element name="td">
						<xsl:value-of select="@moduleid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@modulename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@read">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">read</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@read='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								查看
							</xsl:element>
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@edit">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">edit</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@edit='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								修改
							</xsl:element>
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@insert">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">insert</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@insert='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								添加
							</xsl:element>
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@delete">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">delete</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@delete='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								删除
							</xsl:element>
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@print">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">print</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@print='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								打印
							</xsl:element>
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@check">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">check</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@check='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								审核一
							</xsl:element>
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@confirm">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">confirm</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@confirm='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								审核二
							</xsl:element>
						</xsl:if>
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@verify">
							<xsl:element name="input">
								<xsl:attribute name="type">checkbox</xsl:attribute>
								<xsl:attribute name="roleid"><xsl:value-of
									select="@roleid" /></xsl:attribute>
								<xsl:attribute name="moduleid"><xsl:value-of
									select="@moduleid" /></xsl:attribute>
								<xsl:attribute name="operation">verify</xsl:attribute>
								<xsl:attribute name="onclick">clickbox(this)</xsl:attribute>
								<xsl:if test="@verify='1'">
									<xsl:attribute name="checked">true</xsl:attribute>
								</xsl:if>
								审核三
							</xsl:element>
						</xsl:if>
					</xsl:element>

				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


