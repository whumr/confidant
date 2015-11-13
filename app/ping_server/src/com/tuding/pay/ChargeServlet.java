package com.tuding.pay;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pingplusplus.model.Charge;
import com.tuding.pay.util.ChargeUtil;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/charge")
public class ChargeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public ChargeServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ChargeUtil chargeUtil = ChargeUtil.getInstance();
		try {
			checkParam(request, new String[]{"amount", "subject", "body", "channel"});
			int amount = Integer.parseInt(request.getParameter("amount"));
			String subject = request.getParameter("subject");
			String body = request.getParameter("body");
			String order_no = System.currentTimeMillis() + "";
			String channel = request.getParameter("channel");
			String ip = request.getRemoteAddr();
			Charge charge = chargeUtil.createCharge(amount, subject, body, order_no, channel, ip);
			PrintWriter writer = response.getWriter();
			response.setHeader("Content-Type", "text/html;charset=utf-8");
			writer.write(charge.toString());
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Type", "text/html;charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.write("参数不完整");
			writer.flush();
			writer.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void checkParam(HttpServletRequest request, String[] keys) throws IllegalArgumentException {
		for (int i = 0; i < keys.length; i++) {
			String value = request.getParameter(keys[i]);
			if (value == null || value.trim().length() == 0)
				throw new IllegalArgumentException();
		}
	}
}
