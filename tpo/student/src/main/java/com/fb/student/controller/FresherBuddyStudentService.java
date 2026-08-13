package com.fb.student.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.annotation.DocumentList;
import com.annotation.StudentFeeDetails;
import com.beans.FileUploadUtility;
import com.beans.Parent;
import com.dao.CommonDBBean;
import com.email.EmailUtil;
import com.google.zxing.WriterException;
import com.hibernate.Company;
import com.hibernate.HallTicket;
import com.hibernate.HallTicketConnect;
import com.hibernate.Notice;
import com.hibernate.Registration;
import com.hibernate.Result;
import com.lowagie.text.pdf.codec.Base64;
import com.pdf.generator.GenerateQRCode;
import com.pdf.generator.PDFGenerator;
import com.util.AES;
import com.util.CCPConstant;
import com.util.IMAGECONS;
import com.util.TpoUtil;

@RestController
@RequestMapping("FresherBuddyStudentService")
public class FresherBuddyStudentService extends Parent {

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	EmailUtil emailUtil;

	@Context
	HttpServletRequest request;

	@Autowired
	CommonDBBean commonDBBean;

	@Autowired
	FileUploadUtility fileUploadUtility;

	@Autowired
	PDFGenerator pdfGenerator;

	@PostMapping(path = "/validateMyLogin", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String validateMyLogin(@RequestBody String content) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(content);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmetNo");
			String password = (String) jsonObject.get("password");
			if (commonDBBean != null) {
				char status = commonDBBean.validateStudentLogin(enrollmetNo, password);
				jsonObject = new JSONObject();
				jsonObject.put("status", status);
				response = jsonObject.toJSONString();
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/changePassword", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String changePassword(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String userName = (String) jsonObject.get("userName");
			String password = (String) jsonObject.get("password");
			String newPassword = (String) jsonObject.get("newPassword");

			if (commonDBBean != null) {
				try {
					if (commonDBBean.changeStudentPassword(userName, password, newPassword)) {
						response = "SYour password is successfully changed!";
					} else {
						response = "EInvalid Password!";
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/forgotPassword", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String forgotPassword(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmetNo");
			String email = (String) jsonObject.get("email");
			String basePath = (String) jsonObject.get("BASE_PATH");

			if (commonDBBean != null) {
				try {
					if (commonDBBean.sendStudentPassword(enrollmetNo, email, basePath)) {
						response = "SPassword Reset email has been send on your email!";
					} else {
						response = "EInvalid Enrollment No or Email!";
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/eventList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String eventList(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String enrollmentNo = (String) jsonObject.get("enrollmentNo");
				List<HallTicket> list = commonDBBean.getOpeningList(enrollmentNo);
				JSONArray eventList = new JSONArray();

				if (list != null && list.size() > 0) {
					for (HallTicket hallTicket : list) {
						Company company = commonDBBean.getCompnay(hallTicket.getCompanyID());
						if (company != null) {
							company.setLogo(commonDBBean.getCompanyePic(String.valueOf(company.getCompanyID())));
							hallTicket.setCompany(company);
						}
						eventList.add(hallTicket);
					}
					response = eventList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/noticList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String noticList(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONArray noticList = new JSONArray();
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String enrollmentNo = (String) jsonObject.get("enrollmentNo");
				Boolean studentSpecific = (Boolean) jsonObject.get("studentSpecific");
				List<Notice> list = commonDBBean.getNoticForStudent(enrollmentNo, studentSpecific);
				if (list != null && list.size() > 0) {
					String serviceURL = getImageServiceUrl();
					for (Notice notic : list) {
						
						if (notic.getFileName1() != null && !"".equals(notic.getFileName1())) {
							byte[] f = fileUploadUtility.downloadFile(serviceURL +"/download",
									notic.getNoticeName() + "_" + notic.getFileName1(), IMAGECONS.notice);
							if (f != null) {
								notic.setFile1(Base64.encodeBytes(f));
							} else {
								notic.setFile1(null);
							}
						}
						
						if (notic.getFileName2() != null && !"".equals(notic.getFileName2())) {
							byte[] f = fileUploadUtility.downloadFile(serviceURL+"/download",
									notic.getNoticeName() + "_" + notic.getFileName2(), IMAGECONS.notice);
							if (f != null) {
								notic.setFile2(Base64.encodeBytes(f));
							} else {
								notic.setFile2(null);
							}
						}
						
						if (notic.getFileName3() != null && !"".equals(notic.getFileName3())) {
							byte[] f = fileUploadUtility.downloadFile(serviceURL+"/download",
									notic.getNoticeName() + "_" + notic.getFileName3(), IMAGECONS.notice);
							if (f != null) {
								notic.setFile3(Base64.encodeBytes(f));
							} else {
								notic.setFile3(null);
							}
						}
						
						if (notic.getFileName4() != null && !"".equals(notic.getFileName4())) {
							byte[] f = fileUploadUtility.downloadFile(serviceURL+"/download",
									notic.getNoticeName() + "_" + notic.getFileName4(), IMAGECONS.notice);
							if (f != null) {
								notic.setFile4(Base64.encodeBytes(f));
							} else {
								notic.setFile4(null);
							}
						}
						
						if (notic.getFileName5() != null && !"".equals(notic.getFileName5())) {
							byte[] f = fileUploadUtility.downloadFile(serviceURL+"/download",
									notic.getNoticeName() + "_" + notic.getFileName5(), IMAGECONS.notice);
							if (f != null) {
								notic.setFile5(Base64.encodeBytes(f));
							} else {
								notic.setFile5(null);
							}
						}
						noticList.add(notic);
					}
					response = noticList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/eligibleEventList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String eligibleEventList(@RequestBody String json) {
		String response = null;
		try {
			if (commonDBBean != null) {
				JSONParser parser = new JSONParser();
				Object obj = parser.parse(json);
				JSONObject jsonObject = (JSONObject) obj;
				String enrollmentNo = (String) jsonObject.get("enrollmentNo");
				List<HallTicketConnect> list = commonDBBean.getHallTicketList(enrollmentNo);
				JSONArray eventList = new JSONArray();

				if (list != null && list.size() > 0) {
					HallTicket hallTicket = null;
					Date date = TpoUtil.getFormatedDateInyyyyMMddHHMMss(Calendar.getInstance().getTime());
					for (HallTicketConnect hallTicketConnect : list) {
						hallTicket = hallTicketConnect.getId().getHallTicket();
						if (hallTicket.getIsActive() && hallTicket.getLastDateToApply().after(date)) {
							Company company = commonDBBean.getCompnay(hallTicket.getCompanyID());
							if (company != null) {
								company.setLogo(commonDBBean.getCompanyePic(String.valueOf(company.getCompanyID())));
								hallTicket.setCompany(company);
							}
							eventList.add(hallTicketConnect);
						}
					}
					response = eventList.toJSONString();
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	public Date getFormatedDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		Date formatedDate = null;
		try {
			formatedDate = sdf.parse(date);
		} catch (java.text.ParseException e) {

			e.printStackTrace();
		}
		return formatedDate;
	}

	@PostMapping(path = "/apply", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String apply(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmetNo");
			Integer hallticketid = Integer.parseInt(String.valueOf(jsonObject.get("hallticketid")));
			Boolean isApplied = (Boolean) jsonObject.get("isApplied");
			if (commonDBBean != null) {
				commonDBBean.changeHallTicketStatus(enrollmetNo, hallticketid, isApplied);
				jsonObject = new JSONObject();
				jsonObject.put("status", true);
				response = jsonObject.toJSONString();
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/download", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String download(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmentNo = (String) jsonObject.get("enrollmetNo");
			Integer hallTicketId = Integer.parseInt(String.valueOf(jsonObject.get("hallticketid")));
			if (commonDBBean != null) {
				Registration registration = commonDBBean.getRegistration(enrollmentNo);
				HallTicket hallTicket = commonDBBean.getHallTicket(hallTicketId);
				hallTicket.setCompany(commonDBBean.getCompnay(hallTicket.getCompanyID()));
				List<HallTicket> list = new ArrayList<HallTicket>(1);
				list.add(hallTicket);
				// JSONArray filesJson = new JSONArray();
				String base64EncodedData = Base64.encodeBytes(
						pdfGenerator.generateHallTicket(registration, list, registration.getLastUpdatedBy()));
				JSONObject fileJSON = new JSONObject();
				fileJSON.put("fileContent", base64EncodedData);
				// filesJson.put(fileJSON);
				// ResponseBuilder responseBuilder =
				// Response.ok().entity(filesJson.toString()).type(MediaType.APPLICATION_JSON_TYPE)
				// ;
				response = fileJSON.toJSONString();
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/downloadResume", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String downloadResume(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmentNo = (String) jsonObject.get("enrollmetNo");
			if (commonDBBean != null) {
				Registration registration = commonDBBean.getRegistration(enrollmentNo);
				JSONObject fileJSON = new JSONObject();
				if (registration != null) {
					String fileName = registration.getPersonalinfo().getResume();
					byte[] resume = fileUploadUtility.downloadFileWithParam(getFileServiceUrl() + "/download", fileName,
							IMAGECONS.student.toString() + enrollmentNo + "/" + IMAGECONS.resume.toString());
					if (resume != null) {
						String[] str = fileName.split("\\.");
						String base64EncodedData = Base64.encodeBytes(resume);
						fileJSON.put("fileContent", base64EncodedData);
						fileJSON.put("fileType", str[1]);
						fileJSON.put("fileName", fileName);
						fileJSON.put("Error", "N");
						response = fileJSON.toJSONString();
					}
				} else {
					fileJSON.put("Error", "Y");
					response = fileJSON.toJSONString();

				}
			}
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/downloadRegistrationForm", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String downloadRegistrationForm(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmentNo = (String) jsonObject.get("enrollmetNo");
			if (commonDBBean != null) {
				Registration registration = commonDBBean.getRegistration(enrollmentNo);

				String base64EncodedData = Base64
						.encodeBytes(pdfGenerator.generateRegistrationForm(registration, commonDBBean));
				JSONObject fileJSON = new JSONObject();
				fileJSON.put("fileContent", base64EncodedData);
				response = fileJSON.toJSONString();
			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/downloadProfile", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String downloadProfile(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmentNo = (String) jsonObject.get("enrollmetNo");
			if (enrollmentNo != null) {

				String base64EncodedData = Base64.encodeBytes(commonDBBean.getStudentProfilePic(enrollmentNo));
				JSONObject fileJSON = new JSONObject();
				fileJSON.put("fileContent", base64EncodedData);
				String base64EncodedDataQr = Base64.encodeBytes(GenerateQRCode.getInstance()
						.createQRImage(AES.symmetricEncrypt(enrollmentNo, TpoUtil.geyKeyInfo()), 125, "png"));
				fileJSON.put("qrCodeImage", base64EncodedDataQr);
				if (commonDBBean != null) {
					Registration registration = commonDBBean.getRegistration(enrollmentNo);
					if (registration != null) {
						if (!registration.getStatus()) {
							fileJSON.put("accuoutStatus", "B");
						} else {
							fileJSON.put("accuoutStatus", "A");
							fileJSON.put("status", registration.getApproved() ? "Approved" : "Pending");
							fileJSON.put("name", registration.getFirstName() + " " + registration.getLastName());
							fileJSON.put("email", registration.getEmail());
							fileJSON.put("emailVerified", registration.getEmailVarified() ? "YES" : "NO");
							fileJSON.put("mobile", registration.getContactinfo().getMobileNumber());
							fileJSON.put("mobileVerified",
									registration.getContactinfo().getNumberVerified() ? "YES" : "NO");
							fileJSON.put("selectedIn", registration.getPersonalinfo().getCompanyName());
							fileJSON.put("backlist", registration.getBackdetails().getBlackList() ? "YES" : "NO");
						}
					}
				}
				// filesJson.put(fileJSON);
				// ResponseBuilder responseBuilder =
				// Response.ok().entity(filesJson.toString()).type(MediaType.APPLICATION_JSON_TYPE)
				// ;
				response = fileJSON.toJSONString();
			}
		} catch (ParseException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/uploadProfileImage", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String uploadProfileImage(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmentNo = (String) jsonObject.get("enrollmetNo");
			String encodedFile = (String) jsonObject.get("encodedFile");
			if (enrollmentNo != null && encodedFile != null) {
				commonDBBean.uploadStudentProfilePic(enrollmentNo, Base64.decode(encodedFile));
				JSONObject fileJSON = new JSONObject();
				fileJSON.put("code", "success");
				response = fileJSON.toJSONString();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/verifyNumber", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String verifyNumber(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmetNo");
			if (commonDBBean != null) {
				try {
					if (commonDBBean.verifyMobileNumber(enrollmetNo)) {
						response = "SYour Mobile number is successfully verifyed!";
					} else {
						response = "EInvalid EnrollmetNo!" + enrollmetNo;
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/resultList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String resultList(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmentNo");

			if (commonDBBean != null) {
				try {
					List<Result> list = commonDBBean.getResultForStudent(enrollmetNo);
					JSONArray resultList = new JSONArray();

					if (list != null && list.size() > 0) {
						for (Result result : list) {

							if (!CCPConstant.Disqualified.equals(result.getResult())) {
								String certFileName = "Certificate_" + result.getTestName() + "_"
										+ result.getId().getLoginname() + "_" + result.getTotalnumbers() + ".pdf";
								byte[] certificate = fileUploadUtility.downloadFileWithParam(
										getFileServiceUrl() + "/download", certFileName,
										IMAGECONS.student.toString() + result.getId().getLoginname() + "/"
												+ IMAGECONS.certificate.toString());
								if (certificate != null) {
									result.setCertificate(certificate);
								} else {
									result.setCertificate(null);
								}
							} else {
								result.setCertificate(null);
							}
							resultList.add(result);
						}
						response = resultList.toJSONString();
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/studentFeetList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String studentFeetList(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmentNo");

			if (commonDBBean != null) {
				try {
					List<StudentFeeDetails> list = commonDBBean.getStudentFeeList(enrollmetNo);
					JSONArray resultList = new JSONArray();

					if (list != null && list.size() > 0) {
						resultList.addAll(list);
						response = resultList.toJSONString();
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/documentList", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String documentList(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmentNo");

			if (commonDBBean != null) {
				try {
					List<DocumentList> list = commonDBBean.getDocumentListForStudent(enrollmetNo);
					JSONArray resultList = new JSONArray();
					if (list != null && list.size() > 0) {
						resultList.addAll(list);
						response = resultList.toJSONString();
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(path = "/downloadDocument", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	public String downloadDocument(@RequestBody String json) {
		String response = null;
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jsonObject = (JSONObject) obj;
			String enrollmetNo = (String) jsonObject.get("enrollmentNo");
			String documentName = (String) jsonObject.get("documentName");
			if (commonDBBean != null) {
				try {
					JSONObject fileJSON = new JSONObject();
					DocumentList doc = commonDBBean.getDocumentForStudent(enrollmetNo, documentName);
					JSONArray resultList = new JSONArray();
					if (doc != null) {
						String base64EncodedData = Base64
								.encodeBytes(doc.getDocument().getBytes(1l, (int) doc.getDocument().length()));
						fileJSON.put("fileContent", base64EncodedData);
						fileJSON.put("Error", "N");
						response = fileJSON.toJSONString();
					} else {
						fileJSON.put("Error", "Y");
						response = fileJSON.toJSONString();
					}
				} catch (HibernateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return response;
	}
}