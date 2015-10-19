<%@ page contentType="text/html;charset=UTF-8" session="false"
	import="com.royalstone.security.*" import="com.royalstone.util.*"
	import="com.royalstone.util.daemon.*"
	import="com.royalstone.util.component.*" import="java.sql.*"
	errorPage="../errorpage/errorpage.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="../AW/runtime/styles/xp/aw.css" rel="stylesheet"
	type="text/css"></link>
<link href="./css.css" rel="stylesheet" type="text/css" />
<script language="javascript" src="../AW/runtime/lib/aw.js"> </script>
<SCRIPT LANGUAGE="VBScript">
<!--

    Sub window_onLoad()
      MsgBox(PRComm1)
      formRead.t1.value = Right(Year(Now),2)
      formRead.t2.value = Month(Now)
      formRead.t3.value = Day(Now)
      formRead.t4.value = 255
      formRead.t5.value = 0
      PRComm1.OpenPort(1)
  
    end sub
    
    Sub B1_onclick()     
      
      Dim v1
      Dim v2
      Dim v3
      Dim v4
      Dim v5
      v1 = CInt(formRead.t1.value)
      v2 = CInt(formRead.t2.value)
      v3 = CInt(formRead.t3.value)
      v4 = CInt(formRead.t4.value)
      v5 = CInt(formRead.t5.value)
      B1.disabled = 1
      call PRComm1.GetCheckResultFS(v1,v2,v3,v4,v5)
    end sub 



    Sub Bt1_onClick()
        PRComm1.GetYiqiNO()
    End sub

    Sub PRComm1_onYiqiNO(tmp)
        MsgBox(tmp)
    End Sub
    
    Sub PRComm1_OnReceive(tmp)
    
      Dim cnt
      cnt = 0
      
      B1.disabled = 0

      Dim str1
      Dim str2
      Dim p
      Dim i
      Dim j

      Dim str_tmp
      Dim int_tmp
      Dim int_tmp1

      
      formRead.tmpdata.value = PRComm1.ResultData 
      str_data = Ucase(formRead.tmpdata.value)

      j = Len(formRead.tmpdata.value)
      
      formRead.rsdata.value = "检测结果" + vbTab+vbTab + "检测时间" + vbTab+vbTab+vbTab + "通道号" + vbTab+vbTab + "检测类型" + vbTab+vbTab + "仪器编号" + vbCrLf

      p = 1
      
      Do While j>p
          
        
      	i = InStr(p,formRead.tmpdata.value,"FEFE")
      	if i<=0 Then Exit Do
      	str1 = Mid(formRead.tmpdata.value,p,(i-p))
        
        
        Select Case i-p
            Case 26
                 str_tmp = Mid(str1,3,6)
                 int_tmp = HexStrToInt(str_tmp)
                 
                 str_tmp = Mid(str1,23,2)
                 int_tmp1 = HexStrToInt(str_tmp)
                 if int_tmp1 = 0 or int_tmp1 = 64 then
                    int_tmp = int_tmp/1000
                 Else
                    int_tmp = int_tmp/100
                 End if
                 
                 formRead.rsdata.value = formRead.rsdata.value + CStr(int_tmp) + vbTab + vbTab

                 formRead.rsdata.value = formRead.rsdata.value + FormatDateTime(DateSerial(HexStrToInt(Mid(str1,9,2)),HexStrToInt(Mid(str1,11,2)),HexStrToInt(Mid(str1,13,2)))) + " "
                 
  
                 formRead.rsdata.value = formRead.rsdata.value + FormatDateTime(TimeSerial(HexStrToInt(Mid(str1,15,2)),HexStrToInt(Mid(str1,17,2)),HexStrToInt(Mid(str1,19,2)))) + vbTab + vbTab
                 
                 
                 str_tmp = Mid(str1,21,2)
                 int_tmp = HexStrToInt(str_tmp)
                 formRead.rsdata.value = formRead.rsdata.value + CStr(int_tmp) + "通道" + vbTab + vbTab

                 Select Case int_tmp1
                        Case 0
                        formRead.rsdata.value = formRead.rsdata.value + "农残" + vbTab
                        Case 1
                        formRead.rsdata.value = formRead.rsdata.value + "甲醛" + vbTab
                        Case 2
                        formRead.rsdata.value = formRead.rsdata.value + "吊白块" + vbTab
                        Case 3
                        formRead.rsdata.value = formRead.rsdata.value + "二氧化硫" + vbTab                        
                        Case 4
                        formRead.rsdata.value = formRead.rsdata.value + "亚硝酸盐" + vbTab
                        Case 5
                        formRead.rsdata.value = formRead.rsdata.value + "硝酸盐" + vbTab
                        Case 6
                        formRead.rsdata.value = formRead.rsdata.value + "蛋白质" + vbTab
                        Case 7
                        formRead.rsdata.value = formRead.rsdata.value + "过氧化氢" + vbTab                 
                        Case 8
                        formRead.rsdata.value = formRead.rsdata.value + "甲醇" + vbTab  
                        Case 9
                        formRead.rsdata.value = formRead.rsdata.value + "余氯" + vbTab
                 End Select
              
                 str_tmp = Mid(str1,25,2)
                 int_tmp = HexStrToInt(str_tmp)
                 formRead.rsdata.value = formRead.rsdata.value + vbTab + CStr(int_tmp) + "号" + vbCrLf
                 
                 cnt = cnt + 1
            
            case 24
                 str_tmp = Mid(str1,3,4)
                 int_tmp = HexStrToInt(str_tmp)
                 
                 str_tmp = Mid(str1,19,2)
                 int_tmp1 = HexStrToInt(str_tmp)
                 int_tmp = int_tmp1*65536 + int_tmp
                 
                 str_tmp = Mid(str1,21,2)
                 int_tmp1 = HexStrToInt(str_tmp)
                 if int_tmp1 = 0 or int_tmp1 = 64 then
                    int_tmp = int_tmp/1000
                 Else
                    int_tmp = int_tmp/100
                 End if
                 
                 formRead.rsdata.value = formRead.rsdata.value + CStr(int_tmp) + vbTab + vbTab

                 formRead.rsdata.value = formRead.rsdata.value + FormatDateTime(DateSerial(HexStrToInt(Mid(str1,7,2)),HexStrToInt(Mid(str1,9,2)),HexStrToInt(Mid(str1,11,2)))) + " "
                 
  
                 formRead.rsdata.value = formRead.rsdata.value + FormatDateTime(TimeSerial(HexStrToInt(Mid(str1,13,2)),HexStrToInt(Mid(str1,15,2)),0)) + vbTab + vbTab
                 
                 
                 str_tmp = Mid(str1,17,2)
                 int_tmp = HexStrToInt(str_tmp)
                 formRead.rsdata.value = formRead.rsdata.value + CStr(int_tmp) + "通道" + vbTab + vbTab

                 Select Case int_tmp1
                        Case 0
                        formRead.rsdata.value = formRead.rsdata.value + "农残" + vbTab
                        Case 1
                        formRead.rsdata.value = formRead.rsdata.value + "甲醛" + vbTab
                        Case 2
                        formRead.rsdata.value = formRead.rsdata.value + "吊白块" + vbTab
                        Case 3
                        formRead.rsdata.value = formRead.rsdata.value + "二氧化硫" + vbTab                        
                        Case 4
                        formRead.rsdata.value = formRead.rsdata.value + "亚硝酸盐" + vbTab
                 End Select
              
                 str_tmp = Mid(str1,23,2)
                 int_tmp = HexStrToInt(str_tmp)
                 formRead.rsdata.value = formRead.rsdata.value + vbTab + CStr(int_tmp) + "号" + vbCrLf
                 
                 cnt = cnt + 1
            
            Case 20

                 if CInt(formRead.t1.value)=255 or (CInt(formRead.t1.value) = HexStrToInt(Mid(str1,7,2)) and CInt(formRead.t2.value) = HexStrToInt(Mid(str1,9,2)) and CInt(formRead.t3.value) = HexStrToInt(Mid(str1,11,2))) then
                
                 str_tmp = Mid(str1,3,4)
                 int_tmp = HexStrToInt(str_tmp)
                 int_tmp = int_tmp/10
                 
                 formRead.rsdata.value = formRead.rsdata.value + CStr(int_tmp) + vbTab + vbTab

                 formRead.rsdata.value = formRead.rsdata.value + FormatDateTime(DateSerial(HexStrToInt(Mid(str1,7,2)),HexStrToInt(Mid(str1,9,2)),HexStrToInt(Mid(str1,11,2)))) + " "
                 
  
                 formRead.rsdata.value = formRead.rsdata.value + FormatDateTime(TimeSerial(HexStrToInt(Mid(str1,13,2)),HexStrToInt(Mid(str1,15,2)),0)) + vbTab + vbTab
                 
                 
                 str_tmp = Mid(str1,17,2)
                 int_tmp = HexStrToInt(str_tmp)
                 formRead.rsdata.value = formRead.rsdata.value + CStr(int_tmp) + "通道" + vbTab + vbTab


                 formRead.rsdata.value = formRead.rsdata.value + "农残" + vbTab

          
                 formRead.rsdata.value = formRead.rsdata.value + vbTab + CStr(0) + "号" + vbCrLf
 
                 cnt = cnt + 1
 
                 end if  
                                     
        End Select         
        

        p = i + 4


      Loop  
      
      MsgBox("读取到" & cnt & "条数据")

      
      formRead.rsflag.value = 1   
      commit.disabled = 0
        
    End Sub

     
    Sub window_onUnLoad()
      PRComm1.ClosePort()
      
    end sub
    
    


    Function HexStrToInt(byVal s)
           
           Dim sl, i
           Dim bit
           Dim i1, i2
           
           i2 = 0
           sl = Len(s)
           for i=1 to sl step 1
              bit = Mid(s,i,1)
              i1 = Asc(bit)
              if i1 >=48 and i1<= 57 Then
                 i1 = i1 - 48
              ElseIf i1>=65 and i1 <=70 Then
                 i1 = i1 - 55
              ElseIf i1>=97 and i1 <=102 Then
                 i1 = i1 - 87
              Else
                 HexStrToInt = 0
                 Exit Function
              End If
              i2 = i2*16 + i1
              
           Next
           
           HexStrToInt = i2
           
    End Function
    
    Function Sleep(byVal time)
        
        Dim starttime
        starttime = Timer
        Do
        
        Loop Until Timer>time + starttime
        
    End Function
              
    
    
    
    
--> 
</SCRIPT>
</head>
<body>
	<object classid="clsid:658F01F0-A105-4BC8-9D6D-018139142183"
		id="PRComm1" width="1" height="1"
		data="DATA:application/x-oleobject;BASE64,8AGPZQWhyEudbQGBORQhg1RQRjAHVFBSQ29tbQZQUkNvbW0ETGVmdAIAA1RvcAIABVdpZHRoAgsGSGVpZ2h0AgUHQ2FwdGlvbgYGUFJDb21tBUNvbG9yBwljbEJ0bkZhY2UMRm9udC5DaGFyc2V0Bw9ERUZBVUxUX0NIQVJTRVQKRm9udC5Db2xvcgcMY2xXaW5kb3dUZXh0C0ZvbnQuSGVpZ2h0AvUJRm9udC5OYW1lBg1NUyBTYW5zIFNlcmlmCkZvbnQuU3R5bGULAA5PbGRDcmVhdGVPcmRlcggNUGl4ZWxzUGVySW5jaAJgClRleHRIZWlnaHQCDQAA">
	</object>

	<font face="黑体" size="4">NC系列农残仪器</font>

	<form name=formRead method="POST"
		action="DaemonDetection?section=postdate">
		<input type=hidden name=rsflag> <input type=hidden name=rsdata>
		<input type=hidden name=tmpdata> <input type="text" name="t1"
			size="1" maxlength="2">年 <input type="text" name="t2"
			size="1" maxlength="2">月 <input type="text" name="t3"
			size="1" maxlength="2">日
		<p>
			<input type="text" name="t4" size="2">检测类型
		<p>
			<input type="text" name="t5" size="2">仪器编号
		<p>
			<input type="button" value=" 读 取 " name="B1"> <input
				type="submit" value=" 提 交 " name="commit" disabled="disabled">
			<input type="submit" value=" 仪器编号 " name="Bt1" style="display: none;">

			<textarea name="checkresult" rows="8" cols="112"
				style="display: none;"></textarea>

			<textarea name="buff" rows="5" cols="55" style="display: none;"></textarea>
	</form>

</body>
</html>