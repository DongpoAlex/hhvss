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
		<table align="center" width="98%" cellspacing="2" cellpadding="2"
			border="1">
			<tr>
				<td>证照名称</td>
				<td>说明</td>
				<td>是否年审</td>
			</tr>
			<tr>
				<th colspan="3" class="title">基本证照</th>
			</tr>
			<xsl:for-each select="/xdoc/xout/list/type1/row">
				<xsl:sort select="ctid" order="ascending" data-type="number" />
				<tr>
					<td>
						《
						<xsl:value-of select="ctname" />
						》
					</td>
					<td>
						<xsl:value-of select="note" />
					</td>
					<td>
						<xsl:if test="yearflag=1">
							需年审
						</xsl:if>
						<xsl:if test="yearflag=0">
							不年审
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<th colspan="3" class="title">品类证照</th>
			</tr>
			<tr>
				<td colspan="3">
				</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/list/type2/cc">
				<tr>
					<th colspan="3" class="title2">
						--
						<xsl:value-of select="@ccname"></xsl:value-of>
					</th>
				</tr>
				<xsl:for-each select="row">
					<tr>
						<td>
							《
							<xsl:value-of select="ctname" />
							》
						</td>
						<td>
							<xsl:value-of select="note" />
						</td>
						<td>
							<xsl:if test="yearflag=1">
								需年审
							</xsl:if>
							<xsl:if test="yearflag=0">
								不年审
							</xsl:if>
						</td>
					</tr>
				</xsl:for-each>
				<tr>
					<td colspan="3">
					</td>
				</tr>
			</xsl:for-each>
			<tr>
				<th colspan="3" class="title">新品、旧品检验类证照</th>
			</tr>
			<xsl:for-each select="/xdoc/xout/list/type3/row">
				<xsl:sort select="ctid" order="ascending" data-type="number" />
				<tr>
					<td>
						《
						<xsl:value-of select="ctname" />
						》
					</td>
					<td>
						<xsl:value-of select="note" />
					</td>
					<td>
						<xsl:if test="yearflag=1">
							需年审
						</xsl:if>
						<xsl:if test="yearflag=0">
							不年审
						</xsl:if>
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

</xsl:stylesheet>


