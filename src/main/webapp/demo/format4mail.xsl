<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />

	<xsl:template match="/xdoc/xout/ReadMail">
		<xsl:call-template name="format_mail" />
	</xsl:template>
	<xsl:template match="/xdoc/xerr">
		<xsl:call-template name="format_error" />
	</xsl:template>
	<xsl:template name="format_mail">

		<div class="mail_head">
			<div>
				<span class="bold">
					标题：
					<xsl:value-of select="title" />
				</span>
				<span class="space"></span>
				<span style="float:right;">
					发送时间：
					<xsl:value-of select="sendtime" />
				</span>
			</div>
			<div>
				<span>
					发件人：
					<xsl:value-of select="sender" />
				</span>
				<span class="space"></span>
				<span>
					收件人：
					<xsl:value-of select="receiptor01" />
				</span>
			</div>

			<xsl:if test="fileid != 0">
				<xsl:element name="div">
					<xsl:attribute name="onclick">
						load_file(<xsl:value-of select="fileid" />);
					</xsl:attribute>
					<xsl:attribute name="class">down_file</xsl:attribute>
					附件：
					<xsl:value-of select="filename" />
				</xsl:element>
			</xsl:if>

		</div>
		<div>
			<xsl:element name="textarea">
				<xsl:attribute name="class">mail_body</xsl:attribute>
				<xsl:attribute name="readonly">true</xsl:attribute>
				<xsl:value-of select="mailbody" />
			</xsl:element>
		</div>

	</xsl:template>
	<xsl:template name="format_error">
		<xsl:if test="code != 0">
			<xsl:value-of select="note" />
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>