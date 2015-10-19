<?xml version="1.0"  encoding="UTF-8"?>

<!-- 此文件用于显示供应商发票单 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<xsl:if test="/xdoc/xerr/code != 0">
			<xsl:element name="div">
				<xsl:attribute name="class">titlewarning</xsl:attribute>
				验证失败:
				<xsl:value-of select="/xdoc/xerr/note" />
				错误代码:
				<xsl:value-of select="/xdoc/xerr/code" />
			</xsl:element>
		</xsl:if>
		<xsl:if test="/xdoc/xerr/code = 0">
			<xsl:element name="div">
				<xsl:attribute name="class">ok</xsl:attribute>
				数据验证通过!请确认数据无误后单击提交按钮
			</xsl:element>
		</xsl:if>
		<xsl:if test="/xdoc/xerr/code = 200">
			<xsl:call-template name="print_sheet" />
		</xsl:if>
		<xsl:if test="/xdoc/xerr/code = 0">
			<xsl:call-template name="print_sheet" />
		</xsl:if>
	</xsl:template>

	<xsl:template name="print_sheet">
		<xsl:element name="table">
			<xsl:attribute name="width">100%</xsl:attribute>
			<xsl:attribute name="cellspacing">1</xsl:attribute>
			<xsl:attribute name="cellpadding">3</xsl:attribute>
			<xsl:attribute name="class">tablebg</xsl:attribute>
			<xsl:element name="tr">
				<xsl:element name="th">
					序号
				</xsl:element>
				<xsl:element name="th">
					发(税)票号码
				</xsl:element>
				<xsl:element name="th">
					发(税)票类型号
				</xsl:element>
				<xsl:element name="th">
					发(税)票开票日期
				</xsl:element>
				<xsl:element name="th">
					税率
				</xsl:element>
				<xsl:element name="th">
					税额
				</xsl:element>
				<xsl:element name="th">
					价额
				</xsl:element>

			</xsl:element>

			<xsl:for-each select="xdoc/xout/row_set/row">
				<xsl:element name="tr">

					<xsl:element name="td">
						<xsl:value-of select="seqno" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">
							<xsl:value-of select="invoiceno/@error" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="invoiceno/@title" />
						</xsl:attribute>
						<xsl:value-of select="invoiceno" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">
							<xsl:value-of select="invoicetype/@error" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="invoicetype/@title" />
						</xsl:attribute>
						<xsl:value-of select="invoicetype" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="class">
							<xsl:value-of select="invoicedate/@error" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="invoicedate/@title" />
						</xsl:attribute>
						<xsl:value-of select="invoicedate" />
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:value-of select="taxrate/@error" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="taxrate/@title" />
						</xsl:attribute>
						<xsl:if test="not(taxrate/@error)">
							<xsl:value-of select="format-number(taxrate,'00.00')" />
						</xsl:if>
						<xsl:if test="(taxrate/@error)">
							<xsl:value-of select="taxrate" />
						</xsl:if>

					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:attribute name="class">
							<xsl:value-of select="taxamt/@error" />
						</xsl:attribute>
						<xsl:attribute name="title">
							<xsl:value-of select="taxamt/@title" />
						</xsl:attribute>
						<xsl:if test="not(taxamt/@error)">
							<xsl:value-of select="format-number(taxamt,'#,###.00')" />
						</xsl:if>
						<xsl:if test="(taxamt/@error)">
							<xsl:value-of select="taxamt" />
						</xsl:if>
					</xsl:element>

					<xsl:element name="td">
						<xsl:attribute name="align">right</xsl:attribute>
						<xsl:value-of select="format-number(taxableamt,'#,###.00')" />
					</xsl:element>

				</xsl:element>
			</xsl:for-each>

		</xsl:element>
	</xsl:template>

</xsl:stylesheet>