<?xml version="1.0"  encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns="http://www.w3.org/1999/xhtml" version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="xdoc/xerr/code !=0 ">
				<xsl:element name="div">
					<xsl:attribute name="class">info</xsl:attribute>
					系统中目前没有发现您要的数据。
				</xsl:element>
			</xsl:when>
			<xsl:when test="xdoc/xerr/code=0">
				<xsl:call-template name="outputHTML" />
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

	<xsl:template name="outputHTML">
		<xsl:element name="table">
			<xsl:attribute name="align">center</xsl:attribute>
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">0</xsl:attribute>
			<xsl:attribute name="cellpadding">0</xsl:attribute>
			<xsl:attribute name="border">0</xsl:attribute>
			<xsl:for-each select="/xdoc/xout/group/task">
				<tr>
					<td
						style="font-size: 14px;font-weight: bold;background-color: #FEF2E0;color: #F79A30;">
						<xsl:if test="@ccid=-1">
							基本证照
						</xsl:if>
						<xsl:if test="@ccid=-2">
							检验类证照
						</xsl:if>
						<xsl:if test="@ccid>0">
							品类证照 -
							<xsl:value-of select="@ccname" />
						</xsl:if>
						：
					</td>
				</tr>
				<tr>
					<td>
						<xsl:for-each select="row">
							<span style="font-size: 14px;font-weight: bold;">
								《
								<xsl:value-of select="ctname" />
								》
							</span>
							<span style="font-size: 12px;">
								提示操作：
								<xsl:if test="sheetid!=''">
									已存在相关证照集：
									<xsl:element name="a">
										<xsl:attribute name="href">#</xsl:attribute>
										<xsl:attribute name="onclick">toSheet('<xsl:value-of
											select="sheetid" />',<xsl:value-of select="type" />)</xsl:attribute>
										<xsl:value-of select="sheetid" />
									</xsl:element>
									<xsl:if test="vendertype=1">
										生产型
									</xsl:if>
									<xsl:if test="vendertype=2">
										代理型：
										<xsl:value-of select="vendertypename" />
									</xsl:if>
									(您可以点击单号，在该单中添加)
									或【
									<xsl:element name="a">
										<xsl:attribute name="href">#</xsl:attribute>
										<xsl:attribute name="onclick">newSheet(<xsl:value-of
											select="type" />)</xsl:attribute>
										新建
									</xsl:element>
									】
									一份新的证照集。
								</xsl:if>
								<xsl:if test="sheetid=''">
									<xsl:if test="../@ccid=-2">
										如果是新商品，请
										【
										<xsl:element name="a">
											<xsl:attribute name="href">#<xsl:value-of
												select="type" /></xsl:attribute>
											<xsl:attribute name="onclick">newSheet(4)</xsl:attribute>
											新建新品证照集
										</xsl:element>
										】
										。如果是已立项商品，请
										【
										<xsl:element name="a">
											<xsl:attribute name="href">#<xsl:value-of
												select="type" /></xsl:attribute>
											<xsl:attribute name="onclick">newSheet(3)</xsl:attribute>
											新建旧品证照集
										</xsl:element>
										】
									</xsl:if>
									<xsl:if test="../@ccid!=-2">
										【
										<xsl:element name="a">
											<xsl:attribute name="href">#<xsl:value-of
												select="type" /></xsl:attribute>
											<xsl:attribute name="onclick">newSheet(
					<xsl:if test="../@ccid=-1">1</xsl:if>
					<xsl:if test="../@ccid=-2">3</xsl:if>
					<xsl:if test="../@ccid>0">2</xsl:if>
					)</xsl:attribute>
											新建
										</xsl:element>
										】
										一份新的证照集。
									</xsl:if>
								</xsl:if>
							</span>
							<p />
						</xsl:for-each>
					</td>
				</tr>
				<tr>
					<td></td>
				</tr>
			</xsl:for-each>
		</xsl:element>

	</xsl:template>

</xsl:stylesheet>


