package com.demit.certifly.Extras

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.demit.certifly.Models.AllCertificatesModel
import com.google.zxing.BarcodeFormat
import com.itextpdf.text.*
import com.itextpdf.text.pdf.FontSelector
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat

object CertificateGenerator {

    private const val TAG = "++doc++"
    fun generateCertificate(
        context: Context,
        certificate: AllCertificatesModel
    ): ByteArrayOutputStream? {
        //Following is the output stream that will hold the pdf
        val baos = ByteArrayOutputStream()
        //Here we are creating a pdf document with a4 paper size
        val doc = Document(PageSize.A3)
        try {
            //Initializing the Pdf writer
            PdfWriter.getInstance(doc, baos)
            with(certificate) {
                //Getting the header Logo image from assets
                val ims = context.assets.open("logo.png")
                val bmp = BitmapFactory.decodeStream(ims)
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val companyLogo = Image.getInstance(stream.toByteArray())
                stream.close()
                companyLogo.alignment = Element.ALIGN_JUSTIFIED_ALL

                //Dynamic qr generation.
                val dob = usr_birth ?: ""
                val dateOfReport = cert_proc_stop ?: ""
                val bmp2 = getQrCode(
                    cert_device_id?.takeLast(13),
                    cert_name ?: "",
                    getFormatDateFromString(dob, false),
                    getFormatDateFromString(dateOfReport, true),
                    getCovidStatus(cert_manual_approved).dropLast(1)
                )

                val stream2 = ByteArrayOutputStream()
                bmp2?.compress(Bitmap.CompressFormat.PNG, 100, stream2)
                val qr = Image.getInstance(stream2.toByteArray())
                stream2.close()
                qr.alignment = Element.ALIGN_RIGHT

                //Line Separator
                val logo_line_seperator = getLineSeparator(2f, 100f)

                //Header Information Table
                val headerTable = PdfPTable(2)
                headerTable.widthPercentage = 100F
                headerTable.setWidths(floatArrayOf(2f, 2f))
                //Row1
                headerTable.addCell(getCell("Randox Health London Ltd", 20f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "URN: " + cert_device_id?.takeLast(13),
                        20f,
                        Font.NORMAL
                    )
                )
                // headerTable.addCell(getCell(cert_device_id?.takeLast(13), 22f, Font.NORMAL))

                //Row2
                headerTable.addCell(getCell("Finsbury House,", 20f, Font.NORMAL))
                headerTable.addCell(getCell("Gender: " + cert_sex ?: "", 20f, Font.NORMAL))
                /*  headerTable.addCell(
                      getCell(
                          cert_sex ?: "",
                          20f,
                          Font.NORMAL
                      )
                  )*/


                //Row3
                headerTable.addCell(getCell("23 Finsbury Circus,", 20f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "Swab Date: " + "$cert_timestamp GMT" ?: "",
                        20f,
                        Font.NORMAL
                    )
                )
                // headerTable.addCell(getCell("$cert_timestamp GMT" ?: "", 20f, Font.NORMAL))
                //Row4
                headerTable.addCell(getCell("London,", 20f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "Date of Report: " + "$cert_proc_stop GMT" ?: "",
                        20f,
                        Font.NORMAL
                    )
                )
                // headerTable.addCell(getCell("$cert_proc_stop GMT" ?: "", 20f, Font.NORMAL))

                //Row5
                headerTable.addCell(getCell("EC2M 7EA", 20f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "Passport Number: " + cert_passport ?: "",
                        20f,
                        Font.NORMAL
                    )
                )
                //headerTable.addCell(getCell(cert_passport ?: "", 20f, Font.NORMAL))

                //Row6
                headerTable.addCell(getCell("+44 (0)2894422413", 20f, Font.NORMAL))
                headerTable.addCell(getCell("", 20f, Font.NORMAL))
                //    headerTable.addCell(getCell("", 20f, Font.NORMAL))


                //Line Separator
                val headTableLineSeparator = getLineSeparator(3f, 100f)

                //Report Heading
                val fs = FontSelector()
                val font = FontFactory.getFont(FontFactory.HELVETICA, 25f)
                font.color = BaseColor(11,0,90)
                font.style = Font.BOLD
                fs.addFont(font)
                val phrase = fs.process("Results report / Certificate")
                val reportHeading = Paragraph(phrase)
                reportHeading.alignment = Element.ALIGN_LEFT

                //Report Body
                val bodyContent =
                    "Dear ${certificate.cert_name ?: ""} , Date of Birth: ${usr_birth ?: ""} Contact Number: ${usr_phone ?: ""}\n\n" +
                            "Your coronavirus (COVID-19) test result is "


                val fsBody = FontSelector()
                val fontBody = FontFactory.getFont(FontFactory.HELVETICA, 20f)
                fontBody.color = BaseColor.BLACK
                fontBody.style = Font.ITALIC
                fsBody.addFont(fontBody)
                val phraseBody = fsBody.process(bodyContent)
                val body = Paragraph(phraseBody)
                body.leading = 21f


                val fontStatus = FontFactory.getFont(FontFactory.HELVETICA, 20f)
                fontStatus.color = BaseColor.BLACK
                fontStatus.style = Font.BOLD
                val phraseStatusChunk = Chunk(getCovidStatus(cert_manual_approved), fontStatus)

                body.add(phraseStatusChunk)

                val bodSubContent = getMessageForCovidStatus(cert_manual_approved) + "\n"
                /*  "For more advice:\n" +
                  "if you reside in the United Kingdom, go to https://www.gov.uk/coronavirus\n\n"*/

                val phraseSubBodyChunk = Chunk(bodSubContent, fontBody)
                body.add(phraseSubBodyChunk)


                //Test Information Table
                val fontTestInfoContent = FontFactory.getFont(FontFactory.HELVETICA, 21f)
                fontTestInfoContent.color = BaseColor.BLACK
                fontTestInfoContent.style = Font.BOLD
                val testMethodHeading = Chunk("Test method: ", fontBody)
                val testMethodContent =
                    Chunk("FlowFlex SARS-coV-2 COVID-19 Antigen Rapid Test\n", fontTestInfoContent)

                val testKitHeading = Chunk("Test Kit: ", fontBody)
                val testKitContent =
                    Chunk(
                        "Antigen Rapid Test, Acon Biotech (Hangzhou) Co. Ltd.\n",
                        fontTestInfoContent
                    )

                val targetHeading = Chunk("Target: ", fontBody)
                val targetContent =
                    Chunk("SARS-coV-2 nucleocapsid antigens\n", fontTestInfoContent)

                val sampleTypeHeading = Chunk("Sample type: ", fontBody)
                val sampleTypeContent =
                    Chunk("Self-test Nasal Swab\n\n", fontTestInfoContent)

                //Test details para
                val testInfoPara = Paragraph()
                testInfoPara.add(testMethodHeading)
                testInfoPara.add(testMethodContent)
                testInfoPara.add(testKitHeading)
                testInfoPara.add(testKitContent)
                testInfoPara.add(targetHeading)
                testInfoPara.add(targetContent)
                testInfoPara.add(sampleTypeHeading)
                testInfoPara.add(sampleTypeContent)
                testInfoPara.leading = 22f

                //Whom the result was generated section
                val interpretedHeading = Chunk("Results interpreted by:\n", fontBody)
                // interpretedHeading.setUnderline(1.5f, -2f)

                val interpreterName = Chunk("Mr M Hussain\n", fontBody)
                //interpreterName.setUnderline(1.5f, -2f)
                val designation =
                    Chunk(
                        "European Health Technology Ltd - UKAS Reference Number 22776\n",
                        fontBody
                    )
                //designation.setUnderline(1.5f, -2f)


                val street = Chunk("167-169 Great Portland Street\n", fontBody)
                //street.setUnderline(1.5f, -2f)

                val floor = Chunk("5th Floor\n", fontBody)
                //floor.setUnderline(1.5f, -2f)

                val city = Chunk("London\n", fontBody)
                //city.setUnderline(1.5f, -2f)

                val area = Chunk("W1W 5PE", fontBody)
                //area.setUnderline(1.5f, -2f)

                //Tester Info
                val testerDetailPara = Paragraph()
                testerDetailPara.add(interpretedHeading)
                testerDetailPara.add(interpreterName)
                testerDetailPara.add(designation)
                testerDetailPara.add(street)
                testerDetailPara.add(floor)
                testerDetailPara.add(city)
                testerDetailPara.add(area)
                testerDetailPara.leading = 25f

                //Disclaimer
                val disclaimerText =
                    "Self-Collection kits are provided by Randox Health. This report shall not be reproduced except in full without approval of Randox Health London Ltd. The test dates of all results will be between the collection date and report date stated within this report."

                val fsDisclaimer = FontSelector()
                val fontDisclaimerBody = FontFactory.getFont(FontFactory.HELVETICA, 18f)
                fontDisclaimerBody.color = BaseColor.BLACK
                fontDisclaimerBody.style = Font.ITALIC
                fsDisclaimer.addFont(fontDisclaimerBody)
                val phraseDisclaimer = fsDisclaimer.process(disclaimerText)
                val disclaimer = Paragraph(phraseDisclaimer)
                disclaimer.leading = 16f

                //Generating the pdf file with the above mentioned values
                doc.open()
                doc.add(Paragraph("\n\n"))
                doc.add(companyLogo)
                doc.add(qr)
                doc.add(Paragraph("\n\n"))
                doc.add(logo_line_seperator)
                doc.add(Paragraph("\n"))
                doc.add(headerTable)
                doc.add(Paragraph("\n"))
                doc.add(headTableLineSeparator)
                doc.add(Paragraph("\n\n"))
                doc.add(reportHeading)
                doc.add(Paragraph("\n"))
                doc.add(body)
                doc.add(testInfoPara)
                doc.add(Paragraph("\n"))
                doc.add(testerDetailPara)
                doc.add(Paragraph("\n"))
                doc.add(disclaimer)

                //Closing the document and byte array output stream to avoid memory leakage
                doc.close()
                baos.close()
                return baos
            }
        } catch (docException: DocumentException) {
            Log.e(TAG, docException.message!!)
        } catch (ioException: IOException) {
            Log.e(TAG, ioException.message!!)
        }

        return null

    }

    fun generateCompatCertificate(certificate: AllCertificatesModel): ByteArrayOutputStream? {
        //Following is the output stream that will hold the pdf
        var baos = ByteArrayOutputStream()
        //Here we are creating a pdf document with a4 paper size
        val doc = Document(PageSize.A3)
        try {
            with(certificate) {
                //Initializing the Pdf writer
                PdfWriter.getInstance(doc, baos)

                //Header Information Table
                val headerTable = PdfPTable(2)
                headerTable.widthPercentage = 100F
                headerTable.setWidths(floatArrayOf(2f, 2f))
                //Row1
                headerTable.addCell(getCell("Randox Health London Ltd", 22f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "URN: " + cert_device_id?.takeLast(13),
                        22f,
                        Font.NORMAL
                    )
                )
                // headerTable.addCell(getCell(cert_device_id?.takeLast(13), 22f, Font.NORMAL))

                //Row2
                headerTable.addCell(getCell("Finsbury House,", 22f, Font.NORMAL))
                headerTable.addCell(getCell("Gender: " + cert_sex ?: "", 22f, Font.NORMAL))
                /*  headerTable.addCell(
                      getCell(
                          cert_sex ?: "",
                          20f,
                          Font.NORMAL
                      )
                  )*/


                //Row3
                headerTable.addCell(getCell("23 Finsbury Circus,", 22f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "Swab Date: " + "$cert_timestamp GMT" ?: "",
                        22f,
                        Font.NORMAL
                    )
                )
                // headerTable.addCell(getCell("$cert_timestamp GMT" ?: "", 20f, Font.NORMAL))
                //Row4
                headerTable.addCell(getCell("London,", 22f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "Date of Report: " + "$cert_proc_stop GMT" ?: "",
                        22f,
                        Font.NORMAL
                    )
                )
                // headerTable.addCell(getCell("$cert_proc_stop GMT" ?: "", 20f, Font.NORMAL))

                //Row5
                headerTable.addCell(getCell("EC2M 7EA", 22f, Font.NORMAL))
                headerTable.addCell(
                    getCell(
                        "Passport Number: " + cert_passport ?: "",
                        22f,
                        Font.NORMAL
                    )
                )
                //headerTable.addCell(getCell(cert_passport ?: "", 20f, Font.NORMAL))

                //Row6
                headerTable.addCell(getCell("+44 (0)2894422413", 22f, Font.NORMAL))
                headerTable.addCell(getCell("", 22f, Font.NORMAL))
                //    headerTable.addCell(getCell("", 20f, Font.NORMAL))


                //Line Separator
                val headTableLineSeparator = getLineSeparator(3f, 100f)

                //Report Heading
                val fs = FontSelector()
                val font = FontFactory.getFont(FontFactory.HELVETICA, 28f)
                font.color = BaseColor(11,0,90)
                font.style = Font.BOLD
                fs.addFont(font)
                val phrase = fs.process("Results report / Certificate")
                val reportHeading = Paragraph(phrase)
                reportHeading.alignment = Element.ALIGN_LEFT

                //Report Body
                val bodyContent =
                    "Dear ${certificate.cert_name ?: ""} , Date of Birth: ${usr_birth ?: ""} Contact Number: ${usr_phone ?: ""}\n\n" +
                            "Your coronavirus (COVID-19) test result is "


                val fsBody = FontSelector()
                val fontBody = FontFactory.getFont(FontFactory.HELVETICA, 23f)
                fontBody.color = BaseColor.BLACK
                fontBody.style = Font.ITALIC
                fsBody.addFont(fontBody)
                val phraseBody = fsBody.process(bodyContent)
                val body = Paragraph(phraseBody)
                body.leading = 24f


                val fontStatus = FontFactory.getFont(FontFactory.HELVETICA, 23f)
                fontStatus.color = BaseColor.BLACK
                fontStatus.style = Font.BOLD
                val phraseStatusChunk = Chunk(getCovidStatus(cert_manual_approved), fontStatus)

                body.add(phraseStatusChunk)

                val bodSubContent = getMessageForCovidStatus(cert_manual_approved) + "\n\n"
                        "For more advice:\n" +
                        "if you reside in the United Kingdom, go to https://www.gov.uk/coronavirus\n\n"

                val phraseSubBodyChunk = Chunk(bodSubContent, fontBody)
                body.add(phraseSubBodyChunk)

                //Generating the pdf from above data
                doc.open()
                doc.add(headerTable)
                doc.add(Paragraph("\n"))
                doc.add(headTableLineSeparator)
                doc.add(Paragraph("\n\n\n"))
                doc.add(reportHeading)
                doc.add(Paragraph("\n\n"))
                doc.add(body)
                doc.close()
                baos.close()
                return baos
            }
        } catch (e: DocumentException) {
            e.printStackTrace();
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    //Helper Methods
    private fun getLineSeparator(lineWidth: Float, linePercent: Float): LineSeparator {
        val separator = LineSeparator()
        separator.lineColor = BaseColor.BLACK
        separator.lineWidth = lineWidth
        separator.percentage = linePercent
        return separator

    }

    private fun getCell(text: String?, size: Float, fontStyle: Int): PdfPCell? {
        val fs = FontSelector()
        val font = FontFactory.getFont(FontFactory.HELVETICA, size)
        font.color = BaseColor.BLACK
        font.style = fontStyle
        fs.addFont(font)
        val phrase = fs.process(text)
        val cell = PdfPCell(phrase)
        cell.horizontalAlignment = Element.ALIGN_LEFT
        cell.borderWidthTop = 0f
        cell.borderWidthRight = 0f
        cell.borderWidthLeft = 0f
        cell.borderWidthBottom = 0f
        return cell
    }

    private fun getMessageForCovidStatus(status: String): String {
        return when (status) {
            "N" -> " meaning you did not have the virus when the test was done.\n" +
                    "Please continue to follow your local government guidelines.\n" +
                    "Contact 112 or 999 for a medical emergency.\nFor more advice: \n" +
                    "If you reside in the United Kingdom, go to https://www.gov.uk/coronavirus\n\n"
            "P" -> " meaning you had the virus when the test was done.\n" +
                    "You must self-isolate straight away.\n"+
                    "Please continue to follow your local government guidelines.\n" +
                    "Contact 112 or 999 for a medical emergency.\nFor more advice: \n" +
                    "If you reside in the United Kingdom, go to https://www.gov.uk/coronavirus\n\n"
            "R" -> "meaning you had the virus when the test was done.\n" +
                    "You must self-isolate straight away.\n"+
                    "Please continue to follow your local government guidelines.\n" +
                    "Contact 112 or 999 for a medical emergency.\nFor more advice: \n" +
                    "If you reside in the United Kingdom, go to https://www.gov.uk/coronavirus\n\n"
            else -> "\nYou’ll have to have another test.\n" +
                    "Please continue to follow your local government guidelines.\n" +
                    "Contact 112 or 999 for a medical emergency.\nFor more advice: \n" +
                    "If you reside in the United Kingdom, go to https://www.gov.uk/coronavirus\n\n"
        }
    }

    private fun getCovidStatus(status: String): String {
        return when (status) {
            "N" -> "Negative,"
            "P" -> "Positive,"
            "R" -> "Positive,"
            else -> "Unclear."
        }
    }

    private fun getQrCode(
        urn: String,
        name: String,
        dateOfBirth: String,
        dateOfReport: String,
        covidTestResult: String
    ): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            val content =
                "Confirmation this is a genuine Randox Health Result Certificate for COVID-19 LFT Test.\n\nURN: $urn\n\nName: $name\n\nDate of Birth: $dateOfBirth\n\nDate of Report: $dateOfReport\n\nResult: $covidTestResult"
            barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 130, 130)
        } catch (exception: Exception) {
            null
        }


    }

    @SuppressLint("SimpleDateFormat")
    private fun getFormatDateFromString(date: String, showTime: Boolean): String {
        val formatType = if (showTime) "dd-MM-yyyy HH:mm" else "dd-MM-yyyy"
        val format = SimpleDateFormat(formatType)
        val formattedDate = format.parse(date)
        val resultFormat = if (showTime) "dd-MMM-yyyy HH:mm" else "dd-MMM-yyyy"
        return formattedDate?.let { SimpleDateFormat(resultFormat).format(it) } ?: run { "" }
    }
}