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
				<xsl:element name="span">
					<xsl:attribute name="class">
						sheethead
					</xsl:attribute>
					<xsl:element name="label">
						单据号：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:value-of select="sheetid" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">
						sheethead
					</xsl:attribute>
					<xsl:element name="label">
						供应商：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:value-of select="venderid" />
					<xsl:value-of select="vendername" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">
						sheethead
					</xsl:attribute>
					<xsl:element name="label">
						制单日期：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:value-of select="editdate" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">
						sheethead
					</xsl:attribute>
					<xsl:element name="label">
						提交日期：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:value-of select="checkdate" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">
						sheethead
					</xsl:attribute>
					<xsl:element name="label">
						联系人：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:value-of select="contact" />
				</xsl:element>

				<xsl:element name="span">
					<xsl:attribute name="class">
						sheethead
					</xsl:attribute>
					<xsl:element name="label">
						联系电话：
					</xsl:element>
				</xsl:element>
				<xsl:element name="span">
					<xsl:value-of select="contacttel" />
				</xsl:element>
			</xsl:element>

		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
