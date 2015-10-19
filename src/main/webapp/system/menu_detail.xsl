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
				<xsl:call-template name="output_self" />
				<xsl:element name="br"></xsl:element>
				<xsl:call-template name="output_sibling" />
				<xsl:element name="br"></xsl:element>
				<xsl:element name="a">
					<xsl:attribute name="href">
				  menu_add.jsp?headmenuid=<xsl:value-of
						select="/xdoc/xout/menu_detail/self/menu/@menuid" />
				</xsl:attribute>
					添加子菜单
				</xsl:element>

				<xsl:call-template name="output_children" />
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

	<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->


	<xsl:template name="output_self">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">none</xsl:attribute>
			<xsl:element name="caption">
				菜单信息
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/menu_detail/self/menu">
				<xsl:element name="tr">
					<xsl:attribute name="class">deeper</xsl:attribute>
					<xsl:element name="td">
						<xsl:element name="label">
							菜单号
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@menuid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="label">
							菜单名
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@menulabel" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="label">
							模块功能
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@modulename" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="label">
							模块路径
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@action" />
					</xsl:element>
				</xsl:element>

			</xsl:for-each>


			<xsl:for-each select="/xdoc/xout/menu_detail/root/menu">
				<xsl:element name="tr">
					<xsl:attribute name="class">deeper</xsl:attribute>
					<xsl:element name="td">
						<xsl:element name="label">
							根菜单
						</xsl:element>
					</xsl:element>

					<xsl:element name="td">
						<xsl:value-of select="@menuid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">menu_adm.jsp?menuid=<xsl:value-of
								select="@menuid" /></xsl:attribute>
							<xsl:value-of select="@menulabel" />
						</xsl:element>
					</xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">menuroot_adm.jsp</xsl:attribute>
							维护根菜单
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
			<xsl:for-each select="/xdoc/xout/menu_detail/parent/menu">
				<xsl:element name="tr">
					<xsl:attribute name="class">deeper</xsl:attribute>
					<xsl:element name="td">
						<xsl:element name="label">
							上级菜单
						</xsl:element>
					</xsl:element>

					<xsl:element name="td">
						<xsl:value-of select="@menuid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">menu_adm.jsp?menuid=<xsl:value-of
								select="@menuid" /></xsl:attribute>
							<xsl:value-of select="@menulabel" />
						</xsl:element>
					</xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td"></xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">menu_manager.jsp?menuid=<xsl:value-of
								select="/xdoc/xout/menu_detail/self/menu/@menuid" /></xsl:attribute>
							修改当前菜单
						</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>

		</xsl:element>

	</xsl:template>

	<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->



	<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->

	<xsl:template name="output_children">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">none</xsl:attribute>
			<xsl:element name="caption">
				子菜单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th"></xsl:element>
				<xsl:element name="th"></xsl:element>
				<xsl:element name="th">
					菜单号
				</xsl:element>
				<xsl:element name="th">
					菜单名
				</xsl:element>
				<xsl:element name="th">
					模块号
				</xsl:element>
				<xsl:element name="th">
					模块路径
				</xsl:element>
				<xsl:element name="th">
					显示模型
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/menu_detail/children/menu_list/menu">
				<xsl:sort select="@menuid" order="ascending" data-type="number" />
				<xsl:element name="tr">
					<xsl:attribute name="class">deeper</xsl:attribute>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">javascript:del_menu( &quot;<xsl:value-of
								select="@menuid" />&quot; ) </xsl:attribute>
							删除
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">menu_manager.jsp?menuid=<xsl:value-of
								select="@menuid" /></xsl:attribute>
							修改
						</xsl:element>
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@menuid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">menu_adm.jsp?menuid=<xsl:value-of
								select="@menuid" /></xsl:attribute>
							<xsl:value-of select="@menulabel" />
						</xsl:element>

					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@moduleid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@action" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:if test="@cmid>0 and @menuid>0">
							<xsl:value-of select="@cmidnote" />
							<xsl:element name="a">
								<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								<xsl:attribute name="onclick">cmedit(<xsl:value-of
									select="@moduleid" />,<xsl:value-of select="@menuid" />,<xsl:value-of
									select="@cmid" />)</xsl:attribute>
								维护
							</xsl:element>
						</xsl:if>
						<xsl:if test="@cmid=0 and @menuid>0">
							尚未定义显示模型
							<xsl:element name="a">
								<xsl:attribute name="href">javascript:void(0)</xsl:attribute>
								<xsl:attribute name="onclick">cmedit(<xsl:value-of
									select="@moduleid" />,<xsl:value-of select="@menuid" />,<xsl:value-of
									select="@cmid" />)</xsl:attribute>
								维护
							</xsl:element>
						</xsl:if>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>


	<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->

	<xsl:template name="output_sibling">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="rules">none</xsl:attribute>
			<xsl:element name="caption">
				同级菜单
			</xsl:element>
			<xsl:element name="tr">
				<xsl:element name="th">
					菜单号
				</xsl:element>
				<xsl:element name="th">
					菜单名
				</xsl:element>
				<xsl:element name="th">
					模块号
				</xsl:element>
				<xsl:element name="th">
					模块路径
				</xsl:element>
			</xsl:element>
			<xsl:for-each select="/xdoc/xout/menu_detail/sibling/menu_list/menu">
				<xsl:sort select="@menuid" order="ascending" data-type="number" />
				<xsl:element name="tr">
					<xsl:attribute name="class">deeper</xsl:attribute>
					<xsl:element name="td">
						<xsl:value-of select="@menuid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:element name="a">
							<xsl:attribute name="href">menu_adm.jsp?menuid=<xsl:value-of
								select="@menuid" /></xsl:attribute>
							<xsl:value-of select="@menulabel" />
						</xsl:element>

					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@moduleid" />
					</xsl:element>
					<xsl:element name="td">
						<xsl:value-of select="@action" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

	<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->

</xsl:stylesheet>


