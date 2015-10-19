<?xml version="1.0"  encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output method="xml" version="1.0" indent="yes"
		encoding="UTF-8" />
	<xsl:template match="/">
		<xsl:call-template name="output_list" />
	</xsl:template>

	<xsl:template name="output_list">
		<div class="title">
			课类：
			<xsl:if test="/xdoc/xout/report/row/sgroupid='ALL'">
				全部
			</xsl:if>
			<xsl:if test="/xdoc/xout/report/row/sgroupid!='ALL'">
				<xsl:value-of select="/xdoc/xout/report/row/sgroupid" />
			</xsl:if>
		</div>
		<div class="title">
			供应商编码：
			<xsl:value-of select="/xdoc/xout/report/row/venderid" />
		</div>
		<div class="title">
			供应商名称：
			<xsl:value-of select="/xdoc/xout/report/row/vendername" />
		</div>
		<div class="title">
			数据时间段：
			<xsl:value-of select="/xdoc/xout/report/row/monthid" />
		</div>
		<table border="1" width="100%" class="report">
			<tr>
				<th>项目</th>
				<th>
					<xsl:value-of
						select="number(substring(/xdoc/xout/report/row/monthid,1,4 ))-1" />
					年
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月数据
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月数据
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月预算
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年与
					<xsl:value-of
						select="number(substring(/xdoc/xout/report/row/monthid,1,4 ))-1" />
					年
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月对比
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月预算达成率
				</th>
				<th>
					<xsl:value-of
						select="number(substring(/xdoc/xout/report/row/monthid,1,4 ))-1" />
					年01至
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月数据
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年01至
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月数据
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年01至
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月预算
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年与
					<xsl:value-of
						select="number(substring(/xdoc/xout/report/row/monthid,1,4 ))-1" />
					年01至
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月对比
				</th>
				<th>
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,1,4 )" />
					年01至
					<xsl:value-of select="substring(/xdoc/xout/report/row/monthid,5,6 )" />
					月预算达成率
				</th>
			</tr>
			<tr>
				<td colspan="11" class="item">销售</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/report/row">
				<xsl:if
					test="auxitem='01' or auxitem='02' or auxitem='03' or auxitem='04' or auxitem='05' or auxitem='06' or auxitem='07'">
					<xsl:call-template name="output_listdetail" />
				</xsl:if>
			</xsl:for-each>
			<tr>
				<td colspan="11" class="item">库存</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/report/row">
				<xsl:if test="auxitem='08' or auxitem='09' or auxitem='10'">
					<xsl:call-template name="output_listdetail" />
				</xsl:if>
			</xsl:for-each>
			<tr>
				<td colspan="11" class="item">送货</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/report/row">
				<xsl:if
					test="auxitem='11' or auxitem='12' or auxitem='13' or auxitem='14' or auxitem='15' or auxitem='16' or auxitem='17' or auxitem='18'">
					<xsl:call-template name="output_listdetail" />
				</xsl:if>
			</xsl:for-each>
			<tr>
				<td colspan="11" class="item">其它</td>
			</tr>
			<xsl:for-each select="/xdoc/xout/report/row">
				<xsl:if test="auxitem ='19' or auxitem ='20'">
					<xsl:call-template name="output_listdetail" />
				</xsl:if>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template name="output_listdetail">
		<xsl:if test=" item != '' ">
			<tr>
				<td class="tdHead">
					<xsl:value-of select="item" />
				</td>
				<xsl:if
					test="auxitem='01' or auxitem='02' or auxitem='03' or auxitem='04' or auxitem='06' or auxitem='08' or auxitem='09' or auxitem='11' or auxitem='13' or auxitem='14' or auxitem='16' or auxitem='18' or auxitem='20'">
					<td>
						<xsl:value-of select="format-number(month_lj_ly,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(month_lj,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(budget,'#,##0.00')" />
					</td>
					<td>
						<xsl:if test="month_lj_ly!=0">
							<xsl:value-of
								select="format-number((month_lj div month_lj_ly - 1)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="month_lj_ly=0">
							-
						</xsl:if>
					</td>
					<td>
						<xsl:if test="budget!=0">
							<xsl:value-of select="format-number((month_lj div budget)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="budget=0">
							-
						</xsl:if>
					</td>
					<td>
						<xsl:value-of select="format-number(year_lj_ly,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(year_lj,'#,##0.00')" />
					</td>
					<td>
						<xsl:value-of select="format-number(budget_year,'#,##0.00')" />
					</td>
					<td>
						<xsl:if test="year_lj_ly!=0">
							<xsl:value-of
								select="format-number((year_lj div year_lj_ly - 1)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="year_lj_ly=0">
							-
						</xsl:if>
					</td>
					<td>
						<xsl:if test="budget_year!=0">
							<xsl:value-of
								select="format-number((year_lj div budget_year)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="budget_year=0">
							-
						</xsl:if>
					</td>
				</xsl:if>

				<xsl:if
					test="auxitem='05' or auxitem='07' or auxitem='10' or auxitem='12' or auxitem='15' or auxitem='17' or auxitem='19'">
					<td class="tdHead">
						<xsl:value-of select="format-number(month_lj_ly,'#,##0.00')" />
						%
					</td>
					<td>
						<xsl:value-of select="format-number(month_lj,'#,##0.00')" />
						%
					</td>
					<td>
						<xsl:value-of select="format-number(budget,'#,##0.00')" />
						%
					</td>
					<td>
						<xsl:if test="month_lj_ly!=0">
							<xsl:value-of
								select="format-number((month_lj div month_lj_ly - 1)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="month_lj_ly=0">
							-
						</xsl:if>
					</td>
					<td>
						<xsl:if test="budget!=0">
							<xsl:value-of select="format-number((month_lj div budget)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="budget=0">
							-
						</xsl:if>
					</td>
					<td>
						<xsl:value-of select="format-number(year_lj_ly,'#,##0.00')" />
						%
					</td>
					<td>
						<xsl:value-of select="format-number(year_lj,'#,##0.00')" />
						%
					</td>
					<td>
						<xsl:value-of select="format-number(budget_year,'#,##0.00')" />
						%
					</td>
					<td>
						<xsl:if test="year_lj_ly!=0">
							<xsl:value-of
								select="format-number((year_lj div year_lj_ly - 1)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="year_lj_ly=0">
							-
						</xsl:if>
					</td>
					<td>
						<xsl:if test="budget_year!=0">
							<xsl:value-of
								select="format-number((year_lj div budget_year)*100,'0.00')" />
							%
						</xsl:if>
						<xsl:if test="budget_year=0">
							-
						</xsl:if>
					</td>
				</xsl:if>
			</tr>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>