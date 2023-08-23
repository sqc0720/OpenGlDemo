package com.example.mylibrary.other;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements Renderer {
    private Context mContext;

    // �������
    private Random random = new Random();

    // �������顢��ɫ����
    private FloatBuffer vertices = FloatBuffer.wrap(new float[512 * 3 * 3]);
    private FloatBuffer colors = FloatBuffer.wrap(new float[512 * 3 * 4]);

    // ��ת(x,y,z)
    private float xrot, yrot, zrot;
    // ��ת���ٶ�
    private float xspeed, yspeed, zspeed;
    // x,y,z����ֵ
    private float cx, cy, cz = -5;
    // �Ƿ���εı�־
    private boolean morph = false;
    // �������������
    private int step = 0, steps = 200;
    // ���Ķ�������
    private int maxver;

    // Ҫ�����ε�����
    OBJECT morph1 = new OBJECT();
    OBJECT morph2 = new OBJECT();
    OBJECT morph3 = new OBJECT();
    OBJECT morph4 = new OBJECT();
    // ��������Դ����Ŀ�����
    OBJECT helper = new OBJECT(), sour, dest;

    public FloatBuffer makeFloatBuffer(int length) {
        ByteBuffer bb = ByteBuffer.allocateDirect(length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        return fb;
    }


    public GLRender(Context context) {
        vertices = makeFloatBuffer(512 * 3 * 3);
        colors = makeFloatBuffer(512 * 3 * 4);
        mContext = context;
    }

    private InputStream getFile(String name) {
        AssetManager am = mContext.getResources().getAssets();
        try {
            return am.open(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub

        // ����������Ļ
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // ����ģ����ͼ����
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        //���þ���
        gl.glLoadIdentity();

        // �ӵ�任
        GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);

        draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub

        float ratio = (float) width / height;

        // �����ӿ�(OpenGL�����Ĵ�С)
        gl.glViewport(0, 0, width, height);

        // ����ͶӰ����Ϊ͸��ͶӰ
        gl.glMatrixMode(GL10.GL_PROJECTION);

        // ����ͶӰ������Ϊ��λ����
        gl.glLoadIdentity();

        //����һ��͸��ͶӰ���������ӿڴ�С��
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub

        //����ϵͳ��Ҫ��͸�ӽ�������
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        //����������Ļ����ɫ
        gl.glClearColor(0, 0, 0, 1);

        //������Ȼ���
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // ���û�ϵķ�ʽΪ��͸��
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);

        // ����������Ȼ�������
        gl.glClearDepthf(1.0f);

        // ������Ȳ��Ե�����
        gl.glDepthFunc(GL10.GL_LESS);

        // ����ƽ����Ӱ
        gl.glShadeModel(GL10.GL_SMOOTH);


        initData();
    }

    // ��ʼ������
    private void initData() {
        // ������󶥵���Ϊ0
        maxver = 0;
        // װ��ģ��
        objload("sphere.txt", morph1);
        objload("torus.txt", morph2);
        objload("tube.txt", morph3);

        for (int i = 0; i < 486; i++) {
            // �������һ����ͬ�����������嶥����Ϊ����ʱʹ��
            float xx = ((float) (rand() % 14000) / 1000) - 7;
            float yy = ((float) (rand() % 14000) / 1000) - 7;
            float zz = ((float) (rand() % 14000) / 1000) - 7;

            morph4.points.add(new VERTEX(xx, yy, zz));
        }

        // װ�ظ���ģ��
        objload("sphere.txt", helper);
        sour = dest = morph1;
    }

    // ȡ�������
    public int rand() {
        return Math.abs(random.nextInt());
    }

    // ��ȡһ���ַ���
    public String readstr(BufferedReader br) {
        String str = "";
        try {
            do {
                str = br.readLine();
            }
            while ((str.charAt(0) == '/') || (str.charAt(0) == '\n'));
        } catch (Exception e) {
        }
        return str;
    }

    // ���ļ���װ������(�ļ������������)
    void objload(String name, OBJECT k) {
        // ������
        int ver = 0;
        String oneline;
        int i;

        BufferedReader br = new BufferedReader(new InputStreamReader(getFile(name)));
        //����������
        oneline = readstr(br);
        ver = Integer.valueOf(oneline).intValue();

        k.verts = ver;
        Log.d("zsktest", " k.verts->" + ver);

        // ��ȡÿһ�����������
        for (i = 0; i < ver; i++) {
            oneline = readstr(br);
            String part[] = oneline.trim().split("\\s+");
            float x = Float.valueOf(part[0]);
            float y = Float.valueOf(part[1]);
            float z = Float.valueOf(part[2]);
            VERTEX vertex = new VERTEX(x, y, z);
            Log.d("zsktest", " k.x->" + x + ",y->" + y + ",z->" + z);
            k.points.add(vertex);
        }

        // ���������������󶥵���
        if (ver > maxver) {
            maxver = ver;
        }
    }

    // ����ʱ������˶�
    VERTEX calculate(int i) {
        VERTEX a = new VERTEX(0, 0, 0);

        a.x = (sour.points.get(i).x - dest.points.get(i).x) / steps;
        a.y = (sour.points.get(i).y - dest.points.get(i).y) / steps;
        a.z = (sour.points.get(i).z - dest.points.get(i).z) / steps;

        return a;
    }


    private void draw(GL10 gl) {
        int i;
        float tx, ty, tz;
        VERTEX q = new VERTEX(0, 0, 0);

        // ƽ��
        gl.glTranslatef(cx, cy, cz);
        // ��ת
        gl.glRotatef(xrot, 1, 0, 0);
        gl.glRotatef(yrot, 0, 1, 0);
        gl.glRotatef(zrot, 0, 0, 1);

        // �ı���ת�ĽǶ�
        xrot += xspeed;
        yrot += yspeed;
        zrot += zspeed;

        // �������ö��������������������
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        // ���ö����������ɫ����
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors);

        colors.clear();
        vertices.clear();

        for (i = 0; i < morph1.verts; i++) {
            if (morph) {
                q = calculate(i);
            } else {
                q.x = q.y = q.z = 0;
            }

            // �ƶ�һ��
            helper.points.get(i).x -= q.x;
            helper.points.get(i).y -= q.y;
            helper.points.get(i).z -= q.z;


            tx = helper.points.get(i).x;
            ty = helper.points.get(i).y;
            tz = helper.points.get(i).z;

            // ��ɫ����
            colors.put(0.0f);
            colors.put(1.0f);
            colors.put(1.0f);
            colors.put(1.0f);
            // ��������
            vertices.put(tx);
            vertices.put(ty);
            vertices.put(tz);

            colors.put(0.0f);
            colors.put(0.5f);
            colors.put(1.0f);
            colors.put(1.0f);
            tx -= 2 * q.x;
            ty -= 2 * q.y;
            ty -= 2 * q.y;
            vertices.put(tx);
            vertices.put(ty);
            vertices.put(tz);

            colors.put(1.0f);
            colors.put(0.0f);
            colors.put(0.0f);
            colors.put(1.0f);
            tx -= 2 * q.x;
            ty -= 2 * q.y;
            ty -= 2 * q.y;
            vertices.put(tx);
            vertices.put(ty);
            vertices.put(tz);
        }

        // ����ģ��
        gl.glDrawArrays(GL10.GL_POINTS, 0, morph1.verts * 3);

        // ���ö����������ɫ����
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        if (morph && step <= steps) {
            step++;
        } else {
            morph = false;
            sour = dest;
            step = 0;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                if (!morph) {
                    morph = true;
                    dest = morph1;
                }
                break;
            case KeyEvent.KEYCODE_2:
            case 25:
                if (!morph) {
                    morph = true;
                    dest = morph2;
                }
                break;
            case KeyEvent.KEYCODE_3:
                if (!morph) {
                    morph = true;
                    dest = morph3;
                }
                break;
            case KeyEvent.KEYCODE_4:
                if (!morph) {
                    morph = true;
                    dest = morph4;
                }
                break;
            case KeyEvent.KEYCODE_N:
                zspeed += 0.1f;//������z����ת���ٶ�
                break;
            case KeyEvent.KEYCODE_M:
                zspeed -= 0.1f;//������z����ת���ٶ�
                break;
            case KeyEvent.KEYCODE_Q:
                cz -= 0.1f;// ����Ļ���ƶ�
                break;
            case KeyEvent.KEYCODE_Z:
                cz += 0.1f;// ����Ļ���ƶ�
                break;
            case KeyEvent.KEYCODE_W:
                cy += 0.1f;// �����ƶ�
                break;
            case KeyEvent.KEYCODE_S:
                cy -= 0.1f;// �����ƶ�
                break;
            case KeyEvent.KEYCODE_D:
                cx += 0.1f;// �����ƶ�
                break;
            case KeyEvent.KEYCODE_A:
                cx -= 0.1f;// �����ƶ�
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                xspeed -= 0.1f;// ������x����ת���ٶ�
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                xspeed += 0.1f;// ������x����ת���ٶ�
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                yspeed -= 0.1f;// ������y����ת���ٶ�
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
        }
        return false;
    }

}

class VERTEX {
    // ����(x,y,z)
    float x, y, z;

    public VERTEX(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class OBJECT {
    // ������Ŀ
    int verts;
    // ��������
    List<VERTEX> points = new ArrayList<VERTEX>();
}

