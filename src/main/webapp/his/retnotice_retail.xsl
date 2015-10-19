<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />

	<xsl:template match="/head/row">
		<xsl:call-template name="output_sheethead" />
	</xsl:template>


	<xsl:template name="output_sheethead">
		<xsl:element name="div">
			<xsl:attribute name="class">box</xsl:attribute>
			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">title</xsl:attribute>
					<xsl:value-of select="../@title" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						单据号：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="sheetid" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						供应商：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="venderid" />
					&#160;&#160;
					<xsl:value-of select="vendername" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						退货地：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="shopid" />
					&#160;&#160;
					<xsl:value-of select="shopname" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						退货地址：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="address" />
				</xsl:element>

			</xsl:element>

			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						制单：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="editdate" />
					&#160;&#160;
					<xsl:value-of select="editor" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						审核：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="checkdate" />
					&#160;&#160;
					<xsl:value-of select="checker" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						退货日期:
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="retdate" />
				</xsl:element>
			</xsl:element>

			<xsl:element name="div">
				<xsl:attribute name="class">sheethead</xsl:attribute>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:element name="label">
						备注：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:attribute name="class">sheethead</xsl:attribute>
					<xsl:value-of select="note" />
				</xsl:element>
			</xsl:element>


		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
