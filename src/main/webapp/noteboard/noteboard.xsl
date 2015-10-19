<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">

		<xsl:call-template name="note_detail" />

	</xsl:template>

	<xsl:template name="note_detail">
		<xsl:call-template name="output_title" />
	</xsl:template>


	<xsl:template name="output_title">
		<br />
		公文号：
		<xsl:value-of select="/xdoc/xout/notedetail/noteid" />
		<xsl:element name="br" />
		公文标题：
		<xsl:if test="/xdoc/xout/notedetail/istop = 1">
			<span style="color:red;">[置顶]</span>
		</xsl:if>
		<xsl:value-of select="/xdoc/xout/notedetail/title" />
		<xsl:element name="br" />
		发布日期：
		<xsl:value-of select="/xdoc/xout/notedetail/editdate" />
		截止日期:
		<xsl:value-of select="/xdoc/xout/notedetail/expiredate" />
		<xsl:element name="br" />
		附件:
		<xsl:element name="ul">
			<xsl:for-each select="/xdoc/xout/notedetail/filelist/row">
				<xsl:element name="li">
					<xsl:element name="a">
						<xsl:attribute name="href">#</xsl:attribute>
						<xsl:attribute name="onclick">download_file( <xsl:value-of
							select="fileid" /> )</xsl:attribute>
						<xsl:value-of select="filename" />
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
		---------------------------------------------------------------------------------------------------------------------
	</xsl:template>
</xsl:stylesheet>