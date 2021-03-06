﻿<?xml version="1.0"  encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:call-template name="output_cat" />
	</xsl:template>

	<xsl:template name="output_cat">

		<div class="task">
			<div class="task_title">您需要处理的任务如下：</div>
			<div id="div_catalogue">
				<xsl:element name="ul">
					<xsl:for-each select="/xdoc/xout/catalogue/row">
						<xsl:element name="li">
							<xsl:element name="a">
								<xsl:attribute name="href">javascript:load_module( <xsl:value-of
									select="@moduleid" /> ,'g_status=<xsl:value-of
									select="@g_status" />')
					   	</xsl:attribute>
								<xsl:value-of select="@taskname" />
								:
								<span style="font-weight: bold;font-family: '黑体'">
									<xsl:value-of select="@tasks" />
								</span>
								份
							</xsl:element>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</div>
		</div>

		<xsl:if test="count(/xdoc/xout/catalogue/warning)>0">
			<div class="warning">
				<div class="warning_title">预警提醒</div>
				<div class="warning_catalogue">

					<xsl:for-each select="/xdoc/xout/catalogue/warning">
						<div>
							<xsl:element name="a">
								<xsl:attribute name="href">javascript:load_module( <xsl:value-of
									select="@moduleid" />,'g_status=<xsl:value-of
									select="@status" />' )
				   	</xsl:attribute>
								<xsl:value-of select="@warningname" />
							</xsl:element>
						</div>
						<div style="text-align: center">请及时处理以上单据！</div>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>

	</xsl:template>
</xsl:stylesheet>